package ch.realmtech.server.mod;

import ch.realmtech.server.datactrl.DataCtrl;
import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.registry.Entry;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.Registry;
import ch.realmtech.server.registry.RegistryUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ModLoader {
    private final static Logger logger = LoggerFactory.getLogger(ModLoader.class);
    private final Context context;
    private final Registry<?> rootRegistry;
    private final ModInitializer modInitializerTest;
    private boolean isFail;

    public ModLoader(Context context, Registry<?> rootRegistry, ModInitializer modInitializerTest) {
        this.context = context;
        this.rootRegistry = rootRegistry;
        this.modInitializerTest = modInitializerTest;
    }

    public void initializeCoreMod() throws ModLoaderFail {
        long t1 = System.currentTimeMillis();
        initializeMod(new RealmTechCoreMod());

        if (modInitializerTest == null) {
            try {
                loadModInitializerJar();
            } catch (Exception e) {
                logger.error("Fail to initialize a mod. Error: " + e.getMessage(), e);
            }
        } else {
            initializeMod(modInitializerTest);
        }
        checkName(rootRegistry);

        List<? extends Entry> entries = RegistryUtils.flatEntry(rootRegistry);
        List<Entry> entrySort = sortEntryDependency(rootRegistry, entries);

        for (Entry entry : entrySort) {
            try {
                entry.evaluate(rootRegistry);
            } catch (InvalideEvaluate e) {
                logger.warn("Invalide evaluation for {} entry. Error: {}", entry, e.getMessage());
                isFail = true;
            } catch (Exception e) {
                logger.error("Error during evaluation for {} entry. Error: {}", entry, e.getMessage(), e);
                isFail = true;
            }
        }

        entrySort.forEach(Entry::postEvaluate);

        if (isFail) {
            throw new ModLoaderFail("Can not launch game if mods are not loaded correctly");
        }

        logger.info("All mods are initialized in {}s", (System.currentTimeMillis() - t1) / 1000f);
    }

    private void initializeMod(ModInitializer modInitializer) {
        String modId = modInitializer.getModId();
        Registry<?> modRegistry = Registry.createRegistry(rootRegistry, modId);
        modInitializer.initializeModRegistry(modRegistry, context);
        context.getExecuteOnContext().onClientContext((clientContext) -> {
            if (modInitializer.getClass().isAnnotationPresent(AssetsProvider.class)) {
                try {
                    InputStream atlasResourceAsStream = modInitializer.getClass().getResourceAsStream("/" + modId + ".atlas");
                    InputStream imgResourceAsStream = modInitializer.getClass().getResourceAsStream("/" + modId + ".png");
                    if (atlasResourceAsStream == null) {
                        logger.warn("Asset provider file not found, the atlas should be name \"" + modId + ".atlas" + "\"");
                        isFail = true;
                        if (imgResourceAsStream != null) imgResourceAsStream.close();
                    }
                    if (imgResourceAsStream == null) {
                        logger.warn("Asset provider file not found, the img file should be name \"" + modId + ".png" + "\"");
                        isFail = true;
                        if (atlasResourceAsStream != null) atlasResourceAsStream.close();
                    }
                    if ((atlasResourceAsStream == null || imgResourceAsStream == null)) {
                        return;
                    }
                    Path atlasTempFile = Files.createTempFile(modId, ".atlas");
                    Path imgTempFile = Files.createTempFile(modId, ".png");
                    atlasTempFile = Files.copy(atlasTempFile, Path.of(atlasTempFile.getParent() + "/" + modId + ".atlas"), StandardCopyOption.REPLACE_EXISTING);
                    imgTempFile = Files.copy(imgTempFile, Path.of(atlasTempFile.getParent() + "/" + modId + ".png"), StandardCopyOption.REPLACE_EXISTING);
                    Files.copy(atlasResourceAsStream, atlasTempFile, StandardCopyOption.REPLACE_EXISTING);
                    Files.copy(imgResourceAsStream, imgTempFile, StandardCopyOption.REPLACE_EXISTING);

                    TextureAtlas modTextureAtlas = new TextureAtlas(Gdx.files.absolute(atlasTempFile.toAbsolutePath().toString()));

                    clientContext.onTextureAtlasLoaded((clientTextureAtlas) -> {
                        Array<TextureAtlas.AtlasRegion> regions = modTextureAtlas.getRegions();
                        for (TextureAtlas.AtlasRegion region : regions) {
                            clientTextureAtlas.addRegion(region.name, modTextureAtlas.findRegion(region.name));
                        }
                    });

                } catch (IOException e) {
                    logger.error("Impossible to load atlas from jar file. Mod:" + modId);
                    throw new RuntimeException(e);
                }

            }
        });
    }

    @SuppressWarnings("unchecked")
    private void loadModInitializerJar() throws Exception {
        File[] files = DataCtrl.getModDir().listFiles();
        if (files == null) return;

        ModInitializer[] modInitializers = new ModInitializer[files.length];
        for (int i = 0; i < files.length; i++) {
            try (URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{files[i].toURI().toURL()}, ClassLoader.getSystemClassLoader())) {
                Class<ModInitializer> modInitializerClass = (Class<ModInitializer>) urlClassLoader.loadClass("realmtech.mod.Mod");
                ModInitializer modInitializer = modInitializerClass.getConstructor().newInstance();
                modInitializers[i] = modInitializer;
                initializeMod(modInitializer);
            }
        }

        Set<String> modIdDuplicated = new HashSet<>();
        Set<String> set1 = new HashSet<>();

        for (String modId : Stream.of(modInitializers).map(ModInitializer::getModId).toList()) {
            if (!set1.add(modId)) {
                modIdDuplicated.add(modId);
            }
        }

        for (String modId : modIdDuplicated) {
            logger.warn("Mod id duplicated. Id" + modId);
            isFail = true;
        }
    }

    private List<Entry> sortEntryDependency(Registry<?> rootRegistry, List<? extends Entry> entries) {
        DirectedAcyclicGraph<Entry, DefaultEdge> graph = new DirectedAcyclicGraph<>(DefaultEdge.class);
        for (Entry entry : entries) {
            graph.addVertex(entry);
        }
        for (Entry entry : entries) {
            try {
                EvaluateAfter evaluateAfter = entry.getClass().getMethod("evaluate", Registry.class).getAnnotation(EvaluateAfter.class);
                if (evaluateAfter != null) {
                    for (String evaluateAfterQuery : evaluateAfter.value()) {
                        List<? extends Entry> entriesDependent = processEvaluateAnnotation(rootRegistry, evaluateAfterQuery);
                        for (Entry entryDependent : entriesDependent) {
                            try {
                                graph.addEdge(entryDependent, entry);
                            } catch (IllegalArgumentException e) {
                                throw new InvalideEvaluate("Circular dependency, can not depend on " + evaluateAfterQuery);
                            }
                        }
                    }
                    for (Class<? extends Entry> clazz : evaluateAfter.classes()) {
                        Entry entryDependent = RegistryUtils.findEntry(rootRegistry, clazz)
                                .orElseThrow(() -> new InvalideEvaluate("Can not find " + clazz.toString() + " entry"));
                        try {
                            graph.addEdge(entryDependent, entry);
                        } catch (IllegalArgumentException e) {
                            throw new InvalideEvaluate("Circular dependency, can not depend on " + clazz.toString());
                        }
                    }
                }
                EvaluateBefore evaluateBefore = entry.getClass().getMethod("evaluate", Registry.class).getAnnotation(EvaluateBefore.class);
                if (evaluateBefore != null) {
                    for (String evaluateBeforeQuery : evaluateBefore.value()) {
                        List<? extends Entry> entriesDependent = processEvaluateAnnotation(rootRegistry, evaluateBeforeQuery);
                        for (Entry entryDependent : entriesDependent) {
                            try {
                                graph.addEdge(entry, entryDependent);
                            } catch (IllegalArgumentException e) {
                                throw new InvalideEvaluate("Circular dependency, can not depend on " + evaluateBeforeQuery);
                            }
                        }
                    }
                    for (Class<? extends Entry> clazz : evaluateBefore.classes()) {
                        Entry entryDependent = RegistryUtils.findEntry(rootRegistry, clazz)
                                .orElseThrow(() -> new InvalideEvaluate("Can not find " + clazz.toString() + " entry"));
                        try {
                            graph.addEdge(entry, entryDependent);
                        } catch (IllegalArgumentException e) {
                            throw new InvalideEvaluate("Circular dependency, can not depend on " + clazz.toString());
                        }
                    }
                }

            } catch (InvalideEvaluate e) {
                logger.warn("Invalide dependency sort for {} entry. Error: {}", entry, e.getMessage());
                isFail = true;
            } catch (Exception e) {
                logger.error("Error during dependency sort for {} entry. Error: {}", entry, e.getMessage(), e);
                isFail = true;
            }
        }

        return StreamSupport.stream(graph.spliterator(), false).toList();
    }

    private List<? extends Entry> processEvaluateAnnotation(Registry<?> rootRegistry, String evaluateQuery) throws InvalideEvaluate {
        List<? extends Entry> entries;
        if (isTagQuery(evaluateQuery)) {
            entries = RegistryUtils.findEntries(rootRegistry, evaluateQuery);
        } else {
            entries = List.of(RegistryUtils.findEntry(rootRegistry, evaluateQuery).orElseThrow(() -> new InvalideEvaluate("Can not find " + evaluateQuery + " entry")));
        }
        return entries;
    }

    private boolean isTagQuery(String query) {
        return query.startsWith("#");
    }

    private void checkName(Registry<?> rootRegistry) {
        boolean invalideEntryName = RegistryUtils.flatEntry(rootRegistry).stream()
                .filter((entry) -> !entry.getName().matches("[A-Z][a-zA-Z0-9]*"))
                .peek((invalideNameEntry) -> logger.warn("Invalide name for {} entry. Error: {}", invalideNameEntry, "Entry name must begin with upper case"))
                .map((registry) -> true)
                .findAny()
                .orElse(false);

        if (invalideEntryName) {
            isFail = true;
        }

        boolean invalideRegistryName = RegistryUtils.flatRegistries(rootRegistry).stream()
                .filter((registry) -> registry.getName().matches("[A-Z][a-zA-Z0-9]*"))
                .peek((invalideNameEntry) -> logger.warn("Invalide name for {} registry. Error: {}", invalideNameEntry, "Registry name must not begin with upper case"))
                .map((registry) -> true)
                .findAny()
                .orElse(false);

        if (invalideRegistryName) {
            isFail = true;
        }

        boolean invalideRegistryNameDot = rootRegistry.getChildRegistries().stream()
                .anyMatch((childRegistry) -> RegistryUtils.flatRegistries(childRegistry).stream()
                        .filter((registry) -> registry.getName().contains("."))
                        .peek((invalideRegistry) -> logger.warn("Invalide name for {} registry. Error: {}", invalideRegistry, "Registry name must not contains \".\""))
                        .map((registry) -> true)
                        .findAny()
                        .orElse(false)
                );

        if (invalideRegistryNameDot) {
            isFail = true;
        }

        boolean invalideTagName = rootRegistry.getChildRegistries().stream()
                .anyMatch((childRegistry) -> RegistryUtils.flatRegistries(childRegistry).stream()
                        .anyMatch((registry) -> registry.getTags().stream()
                                .filter((tag) -> tag.contains("."))
                                .peek((invalideTag) -> logger.warn("Invalide name for {} tag in registry {}. Error: {}", invalideTag, registry, "Tag name must not contains \".\""))
                                .map((tag) -> true)
                                .findAny()
                                .orElse(false))

                );

        if (invalideTagName) {
            isFail = true;
        }

        HashMap<Integer, List<Entry>> duplicatedEntries = new HashMap<>();
        RegistryUtils.flatEntry(rootRegistry)
                .forEach((entry) -> RegistryUtils.flatEntry(rootRegistry).stream()
                        .filter((entry1) -> entry != entry1 && entry.getId() == entry1.getId())
                        .forEach((invalideEntry) -> {
                            int duplicatedId = invalideEntry.getId();
                            List<Entry> entries = duplicatedEntries.get(duplicatedId);
                            if (entries == null) {
                                duplicatedEntries.put(duplicatedId, new ArrayList<>(List.of(invalideEntry)));
                            } else {
                                entries.add(invalideEntry);
                            }
                        }));
        duplicatedEntries.forEach((duplicatedId, entries) -> logger.warn("Duplicated id for {}, id {}", entries, duplicatedId));

        if (!duplicatedEntries.isEmpty()) {
            isFail = true;
        }
    }
}

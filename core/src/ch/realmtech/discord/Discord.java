package ch.realmtech.discord;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

public class Discord {
    private final Thread mainThread;
    private Thread discordThread;
    private boolean discordThreadRunning;

    public Discord(Thread mainThread) {
        this.mainThread = mainThread;
    }

    public void init() {
        DiscordRPC lib = DiscordRPC.INSTANCE;

        String applicationId = "1127183943930740737";
        String steamId = "";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> System.out.println("Ready!");
        lib.Discord_Initialize(applicationId, handlers, true, steamId);
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        presence.details = "Testing RPC";
        presence.largeImageKey = "bcy7v3k2";
        lib.Discord_UpdatePresence(presence);
        // in a worker thread
        discordThread = new Thread(() -> {
            while (discordThreadRunning && mainThread.isAlive()) {
                lib.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }, "RPC-Callback-Handler");
        discordThreadRunning = true;
        discordThread.start();
    }

    public void stop() {
        discordThreadRunning = false;
    }
}

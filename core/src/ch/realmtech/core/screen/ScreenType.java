package ch.realmtech.core.screen;

public enum ScreenType {
    LOADING(LoadingScreen.class),
    AUTHENTICATE(AuthenticateScreen.class),
    MENU(MenuScreen.class),
    GAME_SCREEN(GameScreen.class),
    SELECTION_DE_SAUVEGARDE(SelectionDeSauvegarde.class),
    GAME_PAUSE(GamePauseScreen.class),
    OPTION(OptionsScreen.class),
    REJOINDRE_MULTI(RejoindreMulti.class),
    CREATE_NEW_WORLD(CreateNewWorldScreen.class);
    public final Class<? extends AbstractScreen> screenClass;

    ScreenType(Class<? extends AbstractScreen> screenClass) {
        this.screenClass = screenClass;
    }
}

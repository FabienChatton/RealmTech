package ch.realmtech.screen;

public enum ScreenType {
    LOADING(Loading.class),
    MENU(Menu.class),
    GAME_SCREEN(GameScreen.class),
    SELECTION_DE_SAUVEGARDE(SelectionDeSauvegarde.class),
    GAME_PAUSE(GamePauseScreen.class),
    OPTION(OptionsScreen.class),
    REJOINDRE_MULTI(RejoindreMulti.class);
    public final Class<? extends AbstractScreen> screenClass;

    ScreenType(Class<? extends AbstractScreen> screenClass) {
        this.screenClass = screenClass;
    }
}

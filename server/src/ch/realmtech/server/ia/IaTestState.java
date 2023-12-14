package ch.realmtech.server.ia;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

public class IaTestState implements State<IaTestTelegraph> {
    @Override
    public void enter(IaTestTelegraph entity) {
        System.out.println("enter");
    }

    @Override
    public void update(IaTestTelegraph entity) {
        System.out.println("update");
    }

    @Override
    public void exit(IaTestTelegraph entity) {
        System.out.println("exit");
    }

    @Override
    public boolean onMessage(IaTestTelegraph entity, Telegram telegram) {
        return false;
    }
}

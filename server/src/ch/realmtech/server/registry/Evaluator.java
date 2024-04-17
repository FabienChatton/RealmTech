package ch.realmtech.server.registry;

public interface Evaluator {
    void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate;
}

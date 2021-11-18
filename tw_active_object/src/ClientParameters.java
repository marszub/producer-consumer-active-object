import activeObject.Proxy;

public final class ClientParameters {
    public final Proxy storage;
    public final int cycles;
    public final int minConsumption;
    public final int maxConsumption;
    public final int operationsBetween;

    public ClientParameters(Proxy storage, int cycles, int minConsumption, int maxConsumption, int operationsBetween) {
        this.storage = storage;
        this.cycles = cycles;
        this.minConsumption = minConsumption;
        this.maxConsumption = maxConsumption;
        this.operationsBetween = operationsBetween;
    }
}

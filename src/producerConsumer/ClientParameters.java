package producerConsumer;

public final class ClientParameters {
    public final int requests;
    public final long calculations;
    public final int minPortion;
    public final int maxPortion;
    public final int operationsQuantum;

    public ClientParameters(int requests, long calculations, int minConsumption, int maxConsumption, int operationsQuantum) {
        this.requests = requests;
        this.calculations = calculations;
        this.minPortion = minConsumption;
        this.maxPortion = maxConsumption;
        this.operationsQuantum = operationsQuantum;
    }

    public String toString(){
        return requests + ", " + calculations + ", " + + minPortion + ", " + maxPortion + ", " + operationsQuantum;
    }
}

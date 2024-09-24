public class NeuralNetwork {
    private Layer[] layers;
    private int[] neuronsPerLayer;
    
    public NeuralNetwork(int[] neuronsPerLayer) {
        this.layers = new Layer[neuronsPerLayer.length-1];
        this.neuronsPerLayer = neuronsPerLayer;
        //input layer is not processed so is not stored
        for (int i = 0; i < layers.length; i++) {
            this.layers[i] = new Layer(neuronsPerLayer[i], neuronsPerLayer[i+1]);
        }
    }

    public float[] processOutputs(float[] inputs) {
        for (int i = 0; i < layers.length; i++) {
            if(i==0) {
                layers[i].forward(inputs);
            }
            else {
                layers[i].forward(layers[i-1].getOutputs());
            }
        }
        return layers[layers.length - 1].getOutputs();
    }
    
    public void mutate(float mutationProbability, float mutationAmount) {
        for (Layer layer : layers) {
            layer.mutate(mutationProbability, mutationAmount);
        }
    }

    @Override
    public NeuralNetwork clone() {
        NeuralNetwork cloneNetwork = new NeuralNetwork(neuronsPerLayer);
        for (int i = 0; i < layers.length; i++) {
            cloneNetwork.layers[i] = layers[i].clone();
        }
        return cloneNetwork;
    }
}

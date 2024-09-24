import java.util.Random;

public class Layer
{
    private float[][] weights;
    private float[] biases;
    private float[] neuronOutputs;
    
    private int numNeurons;
    private int inputsPerNeuron;
    
    public Layer(int inputsPerNeuron, int numNeurons) {
        this.numNeurons = numNeurons;
        this.inputsPerNeuron = inputsPerNeuron;
        
        weights = new float[numNeurons][inputsPerNeuron];
        biases = new float[numNeurons];
        neuronOutputs = new float[numNeurons];
    }
    
    public void forward(float[] inputs) {
        neuronOutputs = new float[numNeurons];
        for(int i = 0; i < numNeurons; i++) {
            for(int j = 0; j < inputsPerNeuron; j++) {
                neuronOutputs[i] += weights[i][j] * inputs[j];
            }
            neuronOutputs[i] += biases[i];
            neuronOutputs[i] = activation(neuronOutputs[i]);
        }
    }
    
    private float activation(float neuronOutput) {
        return 1.0f / (1.0f + (float) Math.exp(-neuronOutput));  // sigmoid function
    }
    
    public void mutate(float mutationProbability, float mutationAmount) {
        Random random = new Random();
        for (int i = 0; i < numNeurons; i++) {
            for (int j = 0; j < inputsPerNeuron; j++) {
                if (random.nextFloat() < mutationProbability) {
                    weights[i][j] += (2 * random.nextFloat() - 1) * mutationAmount;
                }
            }
            if (random.nextFloat() < mutationProbability) {
                biases[i] += (2 * random.nextFloat() - 1) * mutationAmount;
            }
        }
    }

    @Override
    public Layer clone() {
        Layer cloneLayer = new Layer(this.inputsPerNeuron, this.numNeurons);
        for (int i = 0; i < this.weights.length; i++) {
            System.arraycopy(this.weights[i], 0, cloneLayer.weights[i], 0, this.weights[i].length);
        }
        System.arraycopy(this.biases, 0, cloneLayer.biases, 0, this.biases.length);
        return cloneLayer;
    }
    
    public float[] getOutputs() {
        return this.neuronOutputs;
    }
}

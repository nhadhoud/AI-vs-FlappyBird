Flappy Bird game developed with javafx.
Genetic algorithm trains the neural network of each bird by selecting the best performer from each iteration and giving the new batch of birds a mutated clone of this neural network.
Neural network takes the normalised parameters of the horizontal distance to the next pipe, vertical distance to the top pipe, vertical distance to bottom pipe and the birds current velocity.
Neural network has 1 input layer, 1 hidden layer and 1 output layer.
Processing layers (input layer not included) use sigmoid activation function.
A bird jump is decided by the neural network giving a larger value for jump than not jump in the output layer.
No external libraries are used.
Run from Game. 

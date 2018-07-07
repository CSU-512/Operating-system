package com.os;

public class Main {

    public static void main(String[] args) {
	// write your code here
        int[][] intOne = {{1,2},{3,4}}, intTwo = new int[2][2];
        intTwo[0] = intOne[1];
        intTwo[1] = intOne[0];
        for(int[] i : intTwo) {
            for (int j : i)
                System.out.print(j + " ");
            System.out.println();
        }
    }
}

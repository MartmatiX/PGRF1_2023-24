package cz.uhk.fim;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Starter {


    public static void main(String[] args) {
        int width = 1200;
        int height = 900;

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please select task: \n1 - Task 1 and Task 2\n2 - Task 3");
        System.out.print("Your input: ");
        String input = "";
        try {
            input = br.readLine();
        } catch (Exception e) {
            System.out.println("Unexpected exception [" + e + "] occurred, starting First and Second task");
            SwingUtilities.invokeLater(() -> new Canvas(width, height));
        }
        switch (input.trim()) {
            case "1" -> {
                System.out.println("Starting First and Second task");
                SwingUtilities.invokeLater(() -> new Canvas(width, height));
            }
            case "2" -> {
                System.out.println("Starting Third task");
                SwingUtilities.invokeLater(() -> new Space(width, height).start());
            }
            default -> {
                System.out.println("Your input does not match the selection, starting First and Second task");
                SwingUtilities.invokeLater(() -> new Canvas(width, height));
            }
        }
    }

}

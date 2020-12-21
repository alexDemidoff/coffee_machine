package machine;

import java.util.Scanner;

public class CoffeeMachine {

    private static final int WATER_FOR_ESPRESSO = 250;
    private static final int BEANS_FOR_ESPRESSO = 16;
    private static final int COST_OF_ESPRESSO = 4;

    private static final int WATER_FOR_LATTE = 350;
    private static final int MILK_FOR_LATTE = 75;
    private static final int BEANS_FOR_LATTE = 20;
    private static final int COST_OF_LATTE = 7;

    private static final int WATER_FOR_CAPPUCCINO = 200;
    private static final int MILK_FOR_CAPPUCCINO = 100;
    private static final int BEANS_FOR_CAPPUCCINO = 12;
    private static final int COST_OF_CAPPUCCINO = 6;

    private int water = 400;
    private int milk = 540;
    private int beans = 120;
    private int disposableCups = 9;
    private int money = 550;

    private State currentState;

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        CoffeeMachine coffeeMachine = CoffeeMachine.initialize();

        while (coffeeMachine.getState() != State.FINISHING_WORK) {
            coffeeMachine.takeAction(scanner.nextLine());
        }
    }

    private CoffeeMachine() {
        currentState = State.CHOOSING_ACTION;
        printMainMenu();
    }

    public static CoffeeMachine initialize()  {

        return new CoffeeMachine();
    }

    private State getState() {
        return currentState;
    }

    private void printMainMenu() {

        System.out.println("\nWrite action (buy, fill, take, remaining, exit):");
    }

    private void printCoffeeMenu() {

        System.out.println("\nWhat do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:");
    }

    private void takeAction(String action) {

        switch (currentState) {

            case CHOOSING_ACTION:
                choose(action);
                break;
            case CHOOSING_VARIANT_OF_COFFEE:
                buy(action);
                currentState = State.CHOOSING_ACTION;
                break;
            case FILLING_WATER:
                fill(Resource.WATER, action);
                currentState = State.FILLING_MILK;
                break;
            case FILLING_MILK:
                fill(Resource.MILK, action);
                currentState = State.FILLING_BEANS;
                break;
            case FILLING_BEANS:
                fill(Resource.BEANS, action);
                currentState = State.FILLING_CUPS;
                break;
            case FILLING_CUPS:
                fill(Resource.CUPS, action);
                currentState = State.CHOOSING_ACTION;
                break;
        }

        printMessage();
    }

    private void printMessage() {

        switch (currentState) {
            case CHOOSING_ACTION:
                printMainMenu();
                break;
            case CHOOSING_VARIANT_OF_COFFEE:
                printCoffeeMenu();
                break;
            case FILLING_WATER:
                System.out.println("Write how many ml of water do you want to add:");
                break;
            case FILLING_MILK:
                System.out.println("Write how many ml of milk do you want to add:");
                break;
            case FILLING_BEANS:
                System.out.println("Write how many grams of coffee beans do you want to add:");
                break;
            case FILLING_CUPS:
                System.out.println("Write how many disposable cups of coffee do you want to add:");
                break;
        }
    }

    private void choose(String action) {

        switch (action) {
            case "buy":
                currentState = State.CHOOSING_VARIANT_OF_COFFEE;
                break;
            case "fill":
                currentState = State.FILLING_WATER;
                break;
            case "take":
                take();
                break;
            case "remaining":
                printRemaining();
                break;
            case "exit":
                currentState = State.FINISHING_WORK;
                break;
        }
    }

    private void take() {

        System.out.printf("I gave you %d\n", money);
        money = 0;
    }

    private void fill(Resource resource, String amount) {

        int amountInt;

        try {
            amountInt = Integer.parseInt(amount);

            switch (resource) {
                case WATER:
                    water += amountInt;
                    break;
                case MILK:
                    milk += amountInt;
                    break;
                case BEANS:
                    beans += amountInt;
                    break;
                case CUPS:
                    disposableCups += amountInt;
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Resources are not fresh!");
        }
    }

    private void buy(String action) {

        switch (action) {
            case "1":
                makeEspresso();
                break;
            case "2":
                makeLatte();
                break;
            case "3":
                makeCappuccino();
                break;
            case "back":
                break;
        }
    }

    private void makeCappuccino() {

        if (haveEnoughIngredientsFor(Coffee.CAPPUCCINO)) {

            System.out.println("I have enough resources, making you a coffee!");

            receiveMoney(Coffee.CAPPUCCINO);

            put(Resource.WATER, WATER_FOR_CAPPUCCINO);
            put(Resource.MILK, MILK_FOR_CAPPUCCINO);
            put(Resource.BEANS, BEANS_FOR_CAPPUCCINO);

            pourCoffeeIntoCup();
        }
    }

    private void makeLatte() {

        if (haveEnoughIngredientsFor(Coffee.LATTE)) {

            System.out.println("I have enough resources, making you a coffee!");

            receiveMoney(Coffee.LATTE);

            put(Resource.WATER, WATER_FOR_LATTE);
            put(Resource.MILK, MILK_FOR_LATTE);
            put(Resource.BEANS, BEANS_FOR_LATTE);

            pourCoffeeIntoCup();
        }
    }

    private void makeEspresso() {

        if (haveEnoughIngredientsFor(Coffee.ESPRESSO)) {

            System.out.println("I have enough resources, making you a coffee!");

            receiveMoney(Coffee.ESPRESSO);

            put(Resource.WATER, WATER_FOR_ESPRESSO);
            put(Resource.BEANS, BEANS_FOR_ESPRESSO);

            pourCoffeeIntoCup();
        }
    }

    private boolean haveEnoughIngredientsFor(Coffee coffee) {

        switch (coffee) {
            case ESPRESSO:
                return check(Resource.WATER, WATER_FOR_ESPRESSO) &&
                        check(Resource.BEANS, BEANS_FOR_ESPRESSO) &&
                        checkCups();

            case CAPPUCCINO:
                return check(Resource.BEANS, BEANS_FOR_CAPPUCCINO) &&
                        check(Resource.WATER, WATER_FOR_CAPPUCCINO) &&
                        check(Resource.MILK, MILK_FOR_CAPPUCCINO) && checkCups();

            case LATTE:
                return check(Resource.WATER, WATER_FOR_LATTE) &&
                        check(Resource.MILK, MILK_FOR_LATTE) &&
                        check(Resource.BEANS, BEANS_FOR_LATTE) && checkCups();
        }

        return false;
    }

    private boolean checkCups() {

        if (disposableCups < 1) {
            System.out.println("Sorry, not enough disposable cups!");
            return false;
        }

        return true;
    }

    private boolean check(Resource resource, int amount) {

        switch (resource) {
            case WATER:
                if (water < amount) {
                    System.out.println("Sorry, not enough water!");
                    return false;
                }
                break;
            case MILK:
                if (milk < amount) {
                    System.out.println("Sorry, not enough milk!");
                    return false;
                }
                break;
            case BEANS:
                if (beans < amount) {
                    System.out.println("Sorry, not enough coffee beans!");
                    return false;
                }
                break;
        }

        return true;
    }

    private void pourCoffeeIntoCup() {

        disposableCups--;
    }

    private void put(Resource resource, int amount) {

        switch (resource) {
            case WATER:
                water -= amount;
                break;
            case MILK:
                milk -= amount;
                break;
            case BEANS:
                beans -= amount;
                break;
        }
    }

    private void receiveMoney(Coffee coffee) {

        switch (coffee) {
            case ESPRESSO:
                money += COST_OF_ESPRESSO;
                break;
            case CAPPUCCINO:
                money += COST_OF_CAPPUCCINO;
                break;
            case LATTE:
                money += COST_OF_LATTE;
                break;
        }
    }

    private void printRemaining() {
        System.out.println("\nThe coffee machine has:");
        System.out.printf("%d of water\n", water);
        System.out.printf("%d of milk\n", milk);
        System.out.printf("%d of coffee beans\n", beans);
        System.out.printf("%d of disposable cups\n", disposableCups);
        System.out.printf("%d of money\n", money);
    }
}

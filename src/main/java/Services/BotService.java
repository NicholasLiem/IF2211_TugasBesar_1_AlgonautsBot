package Services;

import Enums.*;
import Models.*;
import java.lang.Math;

import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private GameObject target;
    private GameObject worldCenter;
    private Boolean attacking;
    private Boolean targeted;
    private PlayerAction playerAction;
    private GameState gameState;
    private Boolean abON;
    private Boolean eating;

    public BotService() {
        this.playerAction = new PlayerAction();
        this.gameState = new GameState();
        this.attacking = false;
        Position position = new Position();
        position.setX(0);
        position.setY(0);
        this.worldCenter = new GameObject(null, null, null, null, position, null, null, null, null, null, null);
        this.abON = false;
        this.targeted = false;
        this.target = worldCenter;
        this.eating = false;
    }

    public GameObject getBot() {
        return this.bot;
    }

    public void setBot(GameObject bot) {
        this.bot = bot;
    }

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public void computeNextPlayerAction(PlayerAction playerAction) {
        playerAction.action = PlayerActions.FORWARD;
        playerAction.heading = new Random().nextInt(360);
        System.out.println("Current Tick = " + gameState.world.getCurrentTick());

        double distanceFromCenter = getDistanceBetween(bot, worldCenter);
        if ((distanceFromCenter + (1.5 * bot.size)) > gameState.world.getRadius()*6/8) {
            System.out.println("NEAR BORDER");
            this.target = worldCenter;
        }

        playerAction.heading = findingNewTarget();

        /* 
         * Mekanisme Defense Using Shield
         */
        GameObject nearestTorpedoSalvo;
        List<GameObject> torpedoList = getObjectList(ObjectTypes.TORPEDOSALVO);
        if (!torpedoList.isEmpty()) {
            nearestTorpedoSalvo = torpedoList.get(0); 
            if (targeted && 
                getDistanceBetween(bot, nearestTorpedoSalvo) < 50 && 
                bot.size > 30) {
                playerAction.action = PlayerActions.ACTIVATESHIELD;
            } 
        }

        var playerList = getPlayerList();
        int averagePlayerSize = 0;
        if(playerList != null){
            averagePlayerSize = findAverageSize(playerList);
        }

        if (eating && bot.size > 40 && bot.torpedoSalvoCInteger > 0){
            playerAction.setHeading(getHeadingBetween(playerList.get(0)));
            playerAction.action = PlayerActions.FIRETORPEDOES;
            System.out.println("Firing Torpedo");
        }

        System.out.println("AVERAGE PLY SIZE: " + averagePlayerSize);
        if (attacking && !abON && bot.size > target.getSize() + 10) {
            playerAction.action = PlayerActions.STARTAFTERBURNER;
            this.abON = true;
            System.out.println("AfterBurner On");
        } else if ((!attacking || bot.size < 45 || (bot.size < target.getSize() + 10 && target.getGameObjectType() == ObjectTypes.PLAYER)) && abON) {
            playerAction.action = PlayerActions.STOPAFTERBURNER;
            this.abON = false;
            System.out.println("AfterBurner Off");
        } else if (bot.size > 40 && bot.size >= averagePlayerSize  && bot.torpedoSalvoCInteger > 0) {
            playerAction.action = PlayerActions.FIRETORPEDOES;
            System.out.println("Firing Torpedo");
        }

        // 091205
        this.playerAction = playerAction;
    }

    private int findingNewTarget() {
        // getting list of players and sorting it by distance terkecil ke terbesar
        if (!gameState.getGameObjects().isEmpty() || !gameState.getPlayerGameObjects().isEmpty()) {
            int heading;
            var playerList = getPlayerList();
            var foodList = getObjectList(ObjectTypes.FOOD);
            var gasCloudsList = getObjectList(ObjectTypes.GASCLOUD);
            var wormHoleList = getObjectList(ObjectTypes.WORMHOLE);
            var asteroidList = getObjectList(ObjectTypes.ASTEROIDFIELD);
            
            GameObject nearestWormhole;
            if (!wormHoleList.isEmpty()) {
                nearestWormhole = wormHoleList.get(0);
            } else {
                nearestWormhole = null;
            }

            GameObject nearestAsteroid;
            if (!asteroidList.isEmpty()) {
                nearestAsteroid = asteroidList.get(0);
            } else {
                nearestAsteroid = null;
            }

            GameObject nearestGasCloud;
            if (!gasCloudsList.isEmpty()) {
                nearestGasCloud = gasCloudsList.get(0);
            } else {
                nearestGasCloud = null;
            }

            GameObject nearestFood;
            if (!foodList.isEmpty()) {
                nearestFood = foodList.get(0);
            } else {
                nearestFood = null;
            }
            
            if (playerList.isEmpty()){
                System.out.println("YEY MENANG");
            }

            var nearestPlayer = playerList.get(0);
            var headsToNearestPlayer = getHeadingBetween(nearestPlayer);
            var headsToNearestFood = getHeadingBetween(nearestFood);
            // var distanceFromFoodToGasClouds = getDistanceBetween(nearestFood, nearestGasCloud);

            if (nearestPlayer.getSize() > bot.size) {
                this.targeted = true;
                this.attacking = false;
                this.target = nearestFood;
                heading = headsFarFromAttacker(nearestPlayer, foodList, wormHoleList);
                System.out.println("DIKEJAR MUSUH");
            } else if (nearestPlayer.getSize() < bot.size) {
                this.targeted = false;
                this.attacking = true;
                this.target = nearestPlayer;
                heading = headsToNearestPlayer;
                System.out.println("NGEJAR MUSUH");
            } else if (nearestFood != null) {
                this.targeted = false;
                this.attacking = false;
                this.target = nearestFood;
                heading = headsToNearestFood;
                System.out.println("NYARI MAKAN");
            } else {
                // in this case we are in empty area where there is no food or enemy, high
                // chance near border so we go to center
                this.targeted = false;
                this.target = worldCenter;
                heading = getHeadingBetween(worldCenter);
                this.attacking = false;
                System.out.println("KE TENGAH?");
            }



            double distanceTargetFromGasClouds = getDistanceBetween(nearestGasCloud, target);
            if (nearestGasCloud != null) {
                if (distanceTargetFromGasClouds < nearestGasCloud.getSize() + 25) {
                    this.attacking = false;
                    this.targeted = false;
                    heading = headsFarFromAttacker(nearestGasCloud, foodList, wormHoleList);
                    return heading;
                }
            }
            
            var distanceTargetFromWormhole = getDistanceBetween(nearestWormhole, target);
            if (nearestWormhole != null){
                if (distanceTargetFromWormhole < nearestWormhole.getSize() + 25) {
                    this.attacking = false;
                    this.targeted = false;
                    heading = headsFarFromAttacker(nearestWormhole, foodList, wormHoleList);
                    return heading;
                }
            }

            var distanceTargetFromAsteroid = getDistanceBetween(nearestAsteroid, target);
            if (nearestAsteroid != null){
                if (distanceTargetFromAsteroid < nearestAsteroid.getSize() + 24) {
                    this.attacking = false;
                    this.targeted = false;
                    heading = headsFarFromAttacker(nearestAsteroid, foodList, wormHoleList);
                    return heading;
                }
            }
            
            if (target == worldCenter) {
                this.attacking = false;
                this.targeted = false;
                if (targeted) {
                    heading = getHeadingBetween(nearestFood);
                } else {
                    if (nearestFood != null){
                        heading = getHeadingBetween(nearestFood);
                    } else {
                        heading = getHeadingBetween(worldCenter);
                    }
                }
            }

            return heading;
        }
        return getHeadingBetween(worldCenter);
    }

    // calculating the direction we're heading to when there is enemy bigger and
    // near us
    private int headsFarFromAttacker(GameObject enemy, List<GameObject> foodList, List<GameObject> wormHoleList) {
        // int heading = 0;
        int heading;

        GameObject nearestFood = foodList.get(0);
        if (nearestFood == null) {
            heading = headsInverse(enemy);
        }

        GameObject nearestWormHole;
        if (!wormHoleList.isEmpty()) {
            nearestWormHole = wormHoleList.get(0);
        } else {
            nearestWormHole = null;
        }

        double distanceFromEnemy = getDistanceBetween(bot, enemy);
        var foodAndEnemyDistance = getDistanceBetween(nearestFood, enemy);
        double wormHoleAndEnemyDistance = getDistanceBetween(nearestWormHole, enemy);

        GameObject selectedFood = null;
        for(GameObject food : foodList){
            if (Math.abs(getHeadingBetween(food)-getHeadingBetween(enemy)) >= 180 && getDistanceBetween(food, enemy) > enemy.speed){
                selectedFood = food;
                break;
            }
        }
        
        // double foodAndEnemyDistance = getDistanceBetween(bot, nearestFood);
        if (distanceFromEnemy > enemy.speed &&
            foodAndEnemyDistance > distanceFromEnemy &&
            selectedFood != null && nearestFood != null) {
            if (distanceFromEnemy > 200){
                System.out.println("Ambil makanan terdekat");
                heading = getHeadingBetween(nearestFood);
                this.eating = true;
            } else {
                System.out.println("Ambil makanan yang ada di belakang bot dan enemy");
                heading = getHeadingBetween(selectedFood);
                this.eating = true;
            }

            System.out.println("EATING");
        } else if (wormHoleAndEnemyDistance > distanceFromEnemy && 
                    nearestWormHole != null &&
                    enemy.size > bot.size && 
                    distanceFromEnemy < 10 && 
                    bot.size < nearestWormHole.getSize()) {
            heading = getHeadingBetween(nearestWormHole);
            System.out.println("GOING TO WORMHOLE");
            this.eating = false;
        } else if (foodList != null && selectedFood != null) {
            heading = getHeadingBetween(selectedFood);
            System.out.println("GOING AWAY FROM ATTACKER");
            this.eating = false;
        } else {
            heading = getHeadingBetween(worldCenter);
        }
        return heading;
    }

    private int headsInverse(GameObject enemy) {
        return toDegrees(Math.atan2(enemy.position.y - bot.position.y, enemy.position.x - bot.position.x));
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateSelfState();
    }

    private void updateSelfState() {
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream()
                .filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }

    private double getDistanceBetween(GameObject object1, GameObject object2) {
        if (object1 == null || object2 == null){
            return 0;
        }
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private int getHeadingBetween(GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }

    private int findAverageSize(List<GameObject> gameObjects) {
        if (gameObjects.isEmpty()) {
            return 0;
        }
        int totalSize = 0;
        for (GameObject gameObject : gameObjects) {
            totalSize += gameObject.size;
        }
        return totalSize / gameObjects.size();
    }

    private List<GameObject> getPlayerList(){
        return gameState.getPlayerGameObjects().stream()
        .filter(item -> item.getGameObjectType() == ObjectTypes.PLAYER && item.getId() != bot.id)
        .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
        .collect(Collectors.toList());
    }

    private List<GameObject> getObjectList(ObjectTypes ot){
        return gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ot)
                    .sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
    }

}
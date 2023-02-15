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
        
        // if (target != null) {  
        //     if (!gameState.getGameObjects().stream().filter(item -> item.id == target.id).collect(Collectors.toList()).isEmpty()) {
        //         var newTarget = gameState.getGameObjects().stream().filter(item -> item.id == target.id).collect(Collectors.toList()).get(0);
        //         target = newTarget;
        //     } else if (!gameState.getPlayerGameObjects().stream().filter(item -> item.id == target.id)
        //             .collect(Collectors.toList()).isEmpty()) {
        //         var newTarget = gameState.getPlayerGameObjects().stream().filter(item -> item.id == target.id)
        //                 .collect(Collectors.toList()).get(0);
        //         target = newTarget;
        //     }
        // }
        
        // calculating distance from world center
        var distanceFromCenter = getDistanceBetween(bot, worldCenter);
        if ((distanceFromCenter + (1.5 * bot.size)) > gameState.world.getRadius()*7/8) {
            System.out.println("NEAR BORDER");
            this.attacking = false;
            this.target = worldCenter;
        }

        playerAction.heading = findingNewTarget();

        // Torpedo dateng
        GameObject nearestTorpedoSalvo;
        var torpedoList = gameState.getGameObjects().stream()
                         .filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDOSALVO)
                         .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                         .collect(Collectors.toList());

        if (attacking) {
            System.out.println("ATTACKING");
        }
        if (!attacking) {
            System.out.println("ATTACKING IS FALSE");
        }
        if (targeted) {
            System.out.println("TARGETED IS TRUE");
        }

        if (!torpedoList.isEmpty()) {
            nearestTorpedoSalvo = torpedoList.get(0); 
            System.out.println("THERE IS NEAREST TORPEDO");
            System.out.println("Distance BOT TO TORPEDO: " + getDistanceBetween(bot, nearestTorpedoSalvo));
            System.out.println("Speed TORPEDO: " + nearestTorpedoSalvo.getSpeed());
            if (targeted && 
                getDistanceBetween(bot, nearestTorpedoSalvo) < 200 && 
                bot.size > 25) {
                playerAction.action = PlayerActions.ACTIVATESHIELD;
                System.out.println("ACTIVATING SHIELD");
            } 
        }

        System.out.println("APAKAH AB ON?: " + abON);
        System.out.println("BOT SIZE SKRG: " + bot.size);

        if (attacking && !abON && bot.size > target.getSize() + 10) {
            playerAction.action = PlayerActions.STARTAFTERBURNER;
            this.abON = true;
            System.out.println("AfterBurner On");
        } else if ((!attacking || bot.size < 45 || (bot.size < target.getSize() + 10 && target.getGameObjectType() == ObjectTypes.PLAYER)) && abON) {
            playerAction.action = PlayerActions.STOPAFTERBURNER;
            this.abON = false;
            System.out.println("AfterBurner Off");
        } else if ((attacking) && bot.size > 60 && bot.torpedoSalvoCInteger > 0) {
            playerAction.action = PlayerActions.FIRETORPEDOES;
            System.out.println("Firing Torpedo");
        }
        this.playerAction = playerAction;
    }

    private int findingNewTarget() {
        // getting list of players and sorting it by distance terkecil ke terbesar
        if (!gameState.getGameObjects().isEmpty() || !gameState.getPlayerGameObjects().isEmpty()) {
            int heading;
            var playerList = gameState.getPlayerGameObjects().stream()
                    .filter(item -> item.getGameObjectType() == ObjectTypes.PLAYER && item.getId() != bot.id)
                    .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
            var foodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                    .sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
            var gasCloudsList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GASCLOUD)
                    .sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
            var wormHoleList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.WORMHOLE)
                    .sorted(Comparator
                    .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
            
            GameObject nearestWormhole;
            if (!wormHoleList.isEmpty()) {
                nearestWormhole = wormHoleList.get(0);
            } else {
                nearestWormhole = null;
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
                heading = headsFarFromAttacker(nearestPlayer, foodList, wormHoleList);
                this.attacking = false;
                this.targeted = true;
                System.out.println("DIKEJAR MUSUH");
            } else if (nearestPlayer.getSize() < bot.size) {
                heading = headsToNearestPlayer;
                this.target = nearestPlayer;
                this.attacking = true;
                this.targeted = false;
                System.out.println("NGEJAR MUSUH");
            } else if (nearestFood != null) {
                this.targeted = false;
                heading = headsToNearestFood;
                this.target = nearestFood;
                this.attacking = false;
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

            var distanceTargetFromGasClouds = getDistanceBetween(nearestGasCloud, target);
            System.out.println("JARAK KE GAS CLOUD: " + distanceTargetFromGasClouds);
            System.out.println("SIZE DARI GAS CLOUD: " + nearestGasCloud.getSize());
            
            if (nearestGasCloud != null) {
                if (distanceTargetFromGasClouds < nearestGasCloud.getSize() + 5) {
                    System.out.println("AWAY FROM GAS CLOUD");
                    this.attacking = false;
                    heading = headsFarFromAttacker(nearestGasCloud, foodList, wormHoleList);
                    return heading;
                }
            }
            
            var distanceTargetFromWormhole = getDistanceBetween(nearestWormhole, target);
            if (nearestWormhole != null){
                if (distanceTargetFromWormhole < nearestWormhole.getSize() + 10) {
                    System.out.println("AWAY FROM WORMHOLE");
                    this.attacking = false;
                    heading = headsFarFromAttacker(nearestWormhole, foodList, wormHoleList);
                    return heading;
                }
            }
            
            if (target == worldCenter) {
                if (targeted) {
                    heading = headsFarFromAttacker(nearestPlayer, foodList, wormHoleList);
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
        var nearestFood = foodList.get(0);
        if (nearestFood == null) {
            heading = headsInverse(enemy);
        }
        GameObject nearestWormHole;
        if (!wormHoleList.isEmpty()) {
            nearestWormHole = wormHoleList.get(0);
        } else {
            nearestWormHole = null;
        }

        var distanceFromEnemy = getDistanceBetween(bot, enemy);
        // var foodAndEnemyDistance = getDistanceBetween(nearestFood, enemy);
        var wormHoleAndEnemyDistance = getDistanceBetween(nearestWormHole, enemy);

        // parameternya adalah ketika speed enemy lebih kecil daripada distance
        // artinya si enemy gak dalam range yang bisa ngejar kita dengan cepat
        // dan ketika distance food-nya sangat jauh dari attacker, kita bisa makan
        // dengan menjauh secara gak langsung dari enemy yang lebih besar itu
        
        /* Kita akan melakukan "EATING" ketika:
            1. Jarak Dengan Musuh > Kecepatan Musuh
            2. Jarak Makanan dan Musuh > Jarak Bot dgn Musuh
            3. Jarak Wormhole dan Musuh > Jarak Bot dgn Musuh
            4. Jarak Makanan dan Musuh < Jarak Wormhole dan Musuh
        */

        /* 
            foodAndEnemyDistance > distanceFromEnemy && 
            wormHoleAndEnemyDistance > distanceFromEnemy && 
            foodAndEnemyDistance < wormHoleAndEnemyDistance &&
        */
        // Find eligible nearfood
        GameObject selectedFood = null;
        for(GameObject food : foodList){
            if (Math.abs(getHeadingBetween(food)-getHeadingBetween(enemy)) >= 120){
                selectedFood = food;
                break;
            }
        }
        
        // var foodAndEnemyDistance = getDistanceBetween(bot, selectedFood);
        if (distanceFromEnemy > enemy.speed && selectedFood != null) {
            if (distanceFromEnemy > enemy.speed * 3){
                System.out.println("Ambil makanan terdekat");
                heading = getHeadingBetween(nearestFood);
            } else {
                System.out.println("Ambil makanan yang ada di belakang bot dan enemy");
                heading = getHeadingBetween(selectedFood);
            }

            System.out.println("EATING");
            System.out.println("HEADING FOOD: " + getHeadingBetween(nearestFood));
            System.out.println("HEADING ENEMY TERDEKAT: " + getHeadingBetween(enemy));

        /* Kita akan melakukan pergi ke Wormhole ketika:
            1. Jarak Dengan Musuh > Kecepatan Musuh
            2. Jarak Makanan dan Musuh > Jarak Bot dgn Musuh
            3. Jarak Wormhole dan Musuh > Jarak Bot dgn Musuh
            4. Jarak Makanan dan Musuh > Jarak Wormhole dan Musuh
        */
        /* 
            foodAndEnemyDistance > distanceFromEnemy && 
            wormHoleAndEnemyDistance > distanceFromEnemy && 
            foodAndEnemyDistance > wormHoleAndEnemyDistance
        */
        
        } else if (wormHoleAndEnemyDistance > distanceFromEnemy && 
                    nearestWormHole != null &&
                    enemy.size > bot.size && 
                    distanceFromEnemy < 10 && 
                    bot.size < nearestWormHole.getSize()) {
            heading = getHeadingBetween(nearestWormHole);
            System.out.println("GOING TO WORMHOLE");
        } else {
            heading = headsInverse(enemy);
            System.out.println("GOING AWAY FROM ATTACKER");
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

}
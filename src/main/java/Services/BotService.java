package Services;

import Enums.*;
import Models.*;

import java.io.Console;
import java.util.*;
import java.util.stream.*;

import com.fasterxml.jackson.databind.deser.std.EnumMapDeserializer;

public class BotService {
    private GameObject bot;
    private GameObject target;
    private Boolean attacking;
    private Boolean goingToCentre;
    private PlayerAction playerAction;
    private GameState gameState;

    public BotService() {
        this.playerAction = new PlayerAction();
        this.gameState = new GameState();
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
        
        // cek ada player atau gak di sebelahnya  
        if (!gameState.getGameObjects().isEmpty()) {
            // if the player is all nearby sort the player by the smallest size of player so you can eat the player
            var playerList = gameState.getPlayerGameObjects().stream().filter(item -> item.getId() != bot.id && item.getGameObjectType() == ObjectTypes.PLAYER)
                             .sorted(Comparator.comparing(item -> item.getSize()))
                             .collect(Collectors.toList());
            // di sini udah ada list of food dengan distance food dengan player terurut menaik dari terkecil hingga terbesar  
            var foodList = gameState.getGameObjects()
            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
            .sorted(Comparator
            .comparing(item -> getDistanceBetween(bot, item)))
            .collect(Collectors.toList());  
            if (bot.getSize() + 10 > playerList.get(0).getSize()) {
                // action
                // defense if player size is bigger than this player (menghindar) 
                // attack if bigger
                playerAction.heading = getHeadingBetween(playerList.get(0));
            }  else {
                playerAction.heading = getHeadingBetween(foodList.get(0));
            }
            
            // else if (foodList.size() > 0) {
            //     // action : getting food by the smallest distance
            //     playerAction.heading = getHeadingBetween(foodList.get(0));
            // }
            // syntax masih salah
        } 
        //else if (!gameState.getPlayerGameObjects().isEmpty()) {
        //     // what time can we attack? 
        //     // how to check if this player has any weapon? search from the game state? what kind of weapon and what kind of priority weapon do we have to arrange?
        //     // arrange the weapon in the set by its priority (make the array to store weapon by priority?)
        //     // fire the first priority weapon 
        //     // action : attack anytime you can  
        // }



        this.playerAction = playerAction;

    }

    private int findingNewTarget() {
        int heading = 0;
        // getting list of players and sorting it by distance terkecil ke terbesar
        var nearestPlayer = gameState.getPlayerGameObjects().stream().filter(item -> item.getGameObjectType() == ObjectTypes.PLAYER && item.id != bot.id)
                            .sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                            .collect(Collectors.toList()).get(0);
        // getting list of foods and sorting it by distance terkecil ke terbesar 
        var nearestFood = gameState.getGameObjects()
                          .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                          .sorted(Comparator
                          .comparing(item -> getDistanceBetween(bot, item)))
                          .collect(Collectors.toList()).get(0);
        
        
        // getting direction to nearest player of nearest food 
        var headsToNearestPlayer = getHeadingBetween(nearestPlayer);
        var headsToNearestFood = getHeadingBetween(nearestFood);
        
        if (nearestPlayer.getSize() > bot.size) {
            // in this case the nearest player is a threat because it's bigger
            heading = headsFarFromAttacker(nearestPlayer, nearestFood);
            attacking = false;
        } else if (nearestPlayer.getSize() < bot.size) {
            // in this case the nearest player is a prey
            heading = getHeadingBetween(nearestPlayer);
            target = nearestPlayer;
            attacking = true;
        } else if (nearestFood != null) {
            // in this case the nearest player has the same size as our bot, so we chase for foods
            heading = getHeadingBetween(nearestFood);
            target = nearestFood;
            attacking = false;
        } else {
            // in this case we are in empty area where there is no food or enemy, high chance near border so we go to center
            goingToCentre = true;

        }
        if (goingToCentre) {
            heading = getHeadingBetween(nearestPlayer);
        } 

        return heading;
    }

    // calculating the direction we're heading to when there is enemy bigger and near us
    private int headsFarFromAttacker(GameObject enemy, GameObject nearestFood) {
        // nearest food null berarti gak bisa escape pake food equals to running pake opposite direction
        int heading;
        if (nearestFood == null) {
            heading = headsInverse(enemy);
        } 

        var distanceFromEnemy = getDistanceBetween(bot, enemy);
        // check distance between the food and attacker so we can chase the food that is far from the enemy
        var foodAndEnemyDistance = getDistanceBetween(nearestFood, enemy);

        // parameternya adalah ketika speed enemy lebih kecil daripada distance 
        // artinya si enemy gak dalam range yang bisa ngejar kita dengan cepat
        // dan ketika distance food-nya sangat jauh dari attacker, kita bisa makan dengan menjauh secara gak langsung dari enemy yang lebih besar itu
        if (distanceFromEnemy > enemy.speed && foodAndEnemyDistance > distanceFromEnemy) {
            heading = getHeadingBetween(nearestFood);
        } else {
            heading = headsInverse(enemy);
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
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream().filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }

    private double getDistanceBetween(GameObject object1, GameObject object2) {
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

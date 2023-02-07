package Models;

public class World {

  public Position centerPoint;
  public Integer radius;
  public Integer currentTick;

  public World() {
    Position position = new Position();
    position.setX(0);
    position.setY(0);
    this.centerPoint = position;
    this.radius = 300;
    this.currentTick = 0;
  }

  public Position getCenterPoint() {
    return centerPoint;
  }

  public void setCenterPoint(Position centerPoint) {
    this.centerPoint = centerPoint;
  }

  public Integer getRadius() {
    return radius;
  }

  public void setRadius(Integer radius) {
    this.radius = radius;
  }

  public Integer getCurrentTick() {
    return currentTick;
  }

  public void setCurrentTick(Integer currentTick) {
    this.currentTick = currentTick;
  }
}

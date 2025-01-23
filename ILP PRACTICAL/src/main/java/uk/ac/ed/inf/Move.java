package uk.ac.ed.inf;

class Move {
    String orderNo;
    double fromLongitude;
    double fromLatitude;
    double angle;
    double toLongitude;
    double toLatitude;

    public Move(String orderNum, double fromLong, double fromLat, double toLong, double toLat){
        this.orderNo = orderNum;
        this.fromLongitude = fromLong;
        this.fromLatitude = fromLat;
        this.toLongitude = toLong;
        this.toLatitude = toLat;
        double thisangle = Math.toDegrees(Math.atan2(toLongitude - fromLongitude, toLatitude - fromLatitude));
        thisangle = (thisangle + 360) % 360; //to put the angle in positive 0-360
        thisangle = ((360-thisangle) + 90) % 360; // to turn it anti-clockwise and move it by 90
        this.angle = thisangle;
    }

    public void setAngle (double angle){ this.angle = angle; }
}

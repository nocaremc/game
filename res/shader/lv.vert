attribute vec3 Position;

void main() {
    //pass along the position
    gl_Position = vec4(Position, 1.0);
}
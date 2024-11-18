import processing.video.*; // Importar la biblioteca de video

Movie myVideo;

void setup() {
    size(640, 360);
    
    // Cargar el archivo de video desde la carpeta data
    myVideo = new Movie(this, "video.mp4");
    
    // Reproducir automáticamente
    myVideo.loop(); 
}

void draw() {
    background("red");
    
    // Mostrar el fotograma actual del video
    if (myVideo.available()) {
        myVideo.read(); // Leer el próximo fotograma
    }

    tint(255, 100);
    image(myVideo, 0, 0, width, height); // Dibujar el video en pantalla
}

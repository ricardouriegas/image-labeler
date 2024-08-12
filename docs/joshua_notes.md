# Reminders
- One of my main concerns is **resizing**. If we add the **dot tracing** functionality, we will face problems because the points will have `X` and `Y` coordinates. When the user *resizes* the window, those coordinates **will inherently change**. Therefore, we need to decide if the JavaFX program will run only in `fullscreen` mode or if we need to **implement a solution** to handle resizing.

# Notes
- The JavaFX implementation that I coded **doesn't allow** images in `.jpg` and `.jpeg`, so I added a function to convert those files in `.png` files in compilation time.

# Instalattion of GSON: 
```bash
mvn install:install-file -Dfile=/home/jarrazola/Documents/image-labeler/gson-2.11.0.jar -DgroupId=com.google.code.gson -DartifactId=gson -Dversion=2.11.0 -Dpackaging=jar
```
Remember to change the path to your **absolute path**.
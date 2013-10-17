call ant build
copy /V /Y build\image_applet.jar "C:\xampp\htdocs\web_mirror\sites\all\modules\imgupload\jre_vers.jar"
copy /V /Y build\image_applet.jar ..\..\imgupload\jre_vers.jar
jarsigner -storepass openit -keypass openit ./build/image_applet.jar aaron
copy /V /Y build\image_applet.jar "C:\xampp\htdocs\web_mirror\sites\all\modules\imgupload"
copy /V /Y build\image_applet.jar ..\..\imgupload\image_applet.jar
REM copy /V /Y ..\..\imgupload\*.* "C:\xampp\htdocs\web_mirror\sites\all\modules\imgupload"

echo "done"


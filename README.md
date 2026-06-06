# Programa para identificar obstrucciones mínimas para las gráficas bisplit

**_Matemáticas detrás del programa:_**

Una _partición bisplit_ de una gráfica simple G es una 3-coloración (A, B, C) de
G tal que B es completo a C, es decir, una partición de V(G) en conjuntos
independientes A, B, y C tal que cada vértice de B es adyacente a todo vértice
de C. Una gráfica simple es _bisplit_ si admite una partición bisplit.

Claramente, la clase de las gráficas bisplit es hereditaria, es decir, cerrada
bajo subgráficas inducidas. Lo anterior implica que esta clase de gráficas puede
ser caracterizada por medio de una familia de subgráficas inducidas prohibidas
mínimas, las cuales se conocen como las obstrucciones mínimas para las gráficas
bisplit. Estas gráficas son, por definición, gráficas que no son bisplit, pero
tales que todas sus subgráficas inducidas propias son bisplit.

Este programa está diseñado para identificar obstrucciones mínimas de gráficas
bisplit. Cuenta con dos funciones principales:

(1) Identificar si una gráfica particular (en formato g6) es una obstrucción
   mínima para las gráficas bisplit.

   La salida de esta ejecución puede ser una partición bisplit de la gráfica
   recibida, si es que existe, una subgráfica inducida propia que es una
   obstrucción mínima, o el mensaje de que la gráfica recibida es una
   obstrucción mínima.

   Se acompaña esta salida con mensajes que informan sobre la existencia de P5s
   inducidos y subgráficas inducidas isomorfas a la gráfica House (esto es por
   la utilidad que se le dio originalmente al programa)
    
(2) Encontrar todas las obstrucciones mínimas para las gráficas bisplit dentro
   de una lista de gráficas en formato g6.

   Se devuelve la salida como un archivo de texto plano con las obstrucciones
   mínimas identificadas en formato g6.


**_El formato g6 y el software Nauty_**

El formato _graph6_ o _g6_ es una manera compacta de representar y almacenar
gráficas como una cadena de caracteres que usa sólo caracteres ASCII
imprimibles. Los archivos en este formato son archivos de texto que contienen
una gráfica por línea.

La definición formal de este formato puede ser encontrada en [el sitio web de
Brendan McKay](http://users.cecs.anu.edu.au/~bdm/data/formats.html)


El software Nauty se puede instalar siguiendo las instrucciones que se
encuentran en la página web <http://pallini.di.uniroma1.it/>:

1. Descargar el .tar.gz de <http://pallini.di.uniroma1.it/nauty27r1.tar.gz>.

```bash
$ wget http://pallini.di.uniroma1.it/nauty27r1.tar.gz
```

2. Instalarlo

```bash
$ tar xvzf nautyr1.tar.gz
$ cd nauty27r1
$ ./configure
$ make
```

Después de este procedimiento, el directorio **nauty27r1** contiene todos los
ejecutables.

Entre las muchas utilidades de nauty, se puede utilizar para generar listas de
gráficas de un orden especificado de la siguiente manera:

```bash
$ ./geng n > graphs_n.g6
```

simplemente habrá que sustituir *n* por el orden deseado. La lista de gráficas
que devuelve Nauty es un archivo en formato g6.


**_Sobre el programa:_**

La clase principal del repositorio es
src/main/java/BisplitMinimalObstructions/Main.java

El programa está construido usando maven, por lo cual debe ser instalado desde
la carpeta principal (donde se encuentra este README) usando el comando

```bash
$ mvn install
```

La ejecución del programa se logra a través del comando

```bash
$ java -jar target/bisplit-minimal-obstructions-1.0.jar
```
(para procesar una única gráfica) o su variación

```bash
$ java -jar target/bisplit-minimal-obstructions-1.0.jar [archivoOrigen.g6] [archivoDestino.g6]
```
(para procesar una lista de gráficas en formato g6) tras lo cual se desplegará
una lista de 5 posibles rutinas a seleccionar por el usuario:

```shell
Seleccione la acción que desea realizar:

	(0) Decide si una gráfica contiene una obstrucción mínima para las 
	    gráficas bisplit o alguna subgráfica isomorfa a P5 o House

	(1) A partir de una lista de gráficas en formato g6, decidir qué 
	    gráficas en dicha lista son obstrucciones mínimas para las gráficas 
	    bisplit

Escriba el número de su elección seguido de Enter: 

```

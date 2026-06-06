package BisplitMinimalObstructions;

import java.util.*;

/**
 * Clase para alternar entre distintas representaciones de una gráfica, como
 * g6 (String) y matriz de adyacencia (int[][])
 * 
 * Esta clase sólo tiene comportamiento, no cuenta con atributos.
 */
public class Traduccion {


/* # # # # # # # # # # # # #  MÉTODOS  PRINCIPALES  # # # # # # # # # # # # # */


/**
 * El método recibe una gráfica en formato g6 y devuelve la matriz de
 * adyacencia asociada a dicha gráfica.
 * 
 * @param g6 Una gráfica representada en formato g6.
 * @return la matriz de adyacencia de la gráfica g6.
 */
public static int[][] traduce(String g6) {
  char c = g6.charAt(0);
  int n = asciiAentero(c)-63; // n es el orden de la gráfica
  int[][] mat = new int[n][n];
  String x = "";
  for (int i=1; i<g6.length(); i++)
    x = x + aBase2(asciiAentero(g6.charAt(i)));
  int[] aux = {-1,-1};
  int bin = -1;
  for (int i=0; i<(n*(n-1))/2; i++) {
    aux = posicion(i);
    bin = ((int) x.charAt(i))-48;
    mat[aux[1]][aux[0]] = mat[aux[0]][aux[1]] = bin;
  }
  for (int i=0; i<n; i++)
    mat[i][i] = 0;
  return mat;
}

/**
 * El método recibe la matriz de adyacencia de una gráfica G y devuelve una
 * cadena que representa a la gráfica G en formato g6
 * 
 * Restricciones: el orden de la gráfica debe ser a lo más 62
 * 
 * @param matriz la matriz de adyacencia de una gráfica.
 * @return una representación en formato g6 de la gráfica.
 */
public static String traduceAg6(int[][] matriz) {
  int n = matriz.length;
  if (n>62)
    throw new IllegalArgumentException("La matriz excede el tamaño permitido");
  LinkedList<Integer> x = new LinkedList<>();
  for (int c=1; c<n; c++)
    for (int r=0; r<c; r++)
      x.add(matriz[r][c]);
  int residuo = 6-(x.size() % 6);
  if (residuo!=6)
    for (int i=0; i<residuo; i++)
      x.add(0);
  String g6 = "" + (char)(n+63);
  int contador = 0;
  String aux = "";
  for (Integer i : x) {
    aux = aux + i;
    contador++;
    if (contador>=6) {
      int p = aBase10(aux);
      g6 = g6 + (char)(p+63);
      contador = 0;
      aux = "";
    }
  }
  return g6;
}


/* # # # # # # # # # # # # #  MÉTODOS  AUXILIARES  # # # # # # # # # # # # # */


/**
 * Recibe un carácter ASCII, y devuelve su valor entero
 * @param c un carácter cualquiera
 */
public static int asciiAentero (char c) {
  return (int) c;
}

/**
 * Recibe un entero n (entre 63 y 126, incluidos) y devuelve la cadena que
 * representa al entero n-63 escrito en base 2 a 6 dígitos (bigendian)
 * 
 * @param i un entero entre 63 y 126, incluidos
 * @throws IllegalArgumentException si el entero no está en los parámetros
 *                                  requeridos.
 */
private static String aBase2 (int i) {
  if(i<63 || i>126)
    throw new IllegalArgumentException("El entero recibido no está en " +
                                        "los parámetros adecuados");
  i = i-63;
  String s = "";
  if (i!=0) {
    int d = i;
    int c = 0;
    while(d!=0) {
      c = d%2;
      s = c + s;
      d = d/2;
    }
  }
  while(s.length()<6)
    s = 0 + s;
  return s;
}

/**
 * Dada la numeración de las entradas no diagonales e independientes de una
 * matriz de adyacencia:
                          * * * * *...
                          0 * * * *...
                          1 2 * * *...
                          3 4 5 * *...
                          . . . . .
  * el método devuelve en un arreglo de dos enteros la posición asignada al
  * entero que recibe como parámetro.
  * @param i el entero del que se desea conocer la posición
  * @throws IllegalArgumentException si el entero no es positivo.
  */
private static int[] posicion(int i) {
  i = i+1;
  if (i<=0)
    throw new IllegalArgumentException("El entero recibido debe ser no negativo");
  int[] ij = new int[2];
  int contador = 0;
  int n = 1;
  int J = 0;
  while(i != 0) {
    if(i-n>=0) {
      i = i-n;
      J = n;
      n++;
    } else {
      J = i;
      i = 0;
    }
    contador++;
  }
  ij[0]=contador;
  ij[1]=J-1;
  return ij;
}

/**
 * El método recibe una cadena de tamaño 6 de 0s y 1s (un número binario a 6
 * dígitos), y devuelve el entero en base 10 equivalente al binario (bigendian)
 *
 * @throws IllegalArgumentException Si el string no tiene tamaño 6
 */
private static Integer aBase10(String s){
  if (s.length()!=6)
    throw new IllegalArgumentException("Tamaño de la cadena inapropiado");
  int sum = 0;
  for (int i=0; i<6; i++)
    sum += Integer.parseInt(String.valueOf(s.charAt(i)))*Math.pow(2, 5-i);
  return sum;
}

}

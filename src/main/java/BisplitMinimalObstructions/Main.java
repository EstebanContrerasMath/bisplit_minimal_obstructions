package BisplitMinimalObstructions;

import java.util.*;
import java.io.*;

/**
 * Programa para identificar obstrucciones mínimas para las gráficas bisplit
 *
 * 
 * Una gráfica bisplit es aquella cuyo conjunto de vértices admite una
 * 3-coloración propia (R, G, B) tal que cada vértice de G es adyacente a todo
 * vértice de B (i.e. que G es completo a B).
 *
 * Las gráficas bisplit constituyen una familia hereditaria de gráficas (i.e.
 * que es cerrada bajo subgráficas inducidas), por lo que pueden ser
 * caracterizadas por un conjunto (posiblemente infinito) de subgráficas
 * inducidas prohibidas. Las gráficas minimales con la propiedad de no ser
 * bisplit (i.e. aquellas que no son bisplit, pero tales que cualquiera de sus
 * subgráficas propias inducidas sí son bisplit) son las obstrucciones mínimas
 * para las gráficas bisplit.
 *
 * El objetivo de este programa es automatizar verificaciones sobre si una
 * gráfica es bisplit o no, y en caso de que no lo sea determinar alguna
 * obstrucción mínima que contenga.
 *
 * Por el contexto en el que está planteado el uso de este programa, también
 * tiene la capacidad de verificar si las gráficas tienen como subgráfica
 * inducida una trayectoria orden 5 (P5) o su complemento (la gráfica house)
 *
 * 
 * Los algoritmos implementados por este programa no pretender ser óptimos. De
 * hecho, mucho del código de este programa fue adaptado de otro programa
 * escrito previamente para reconocer gráficas unipolares (una gráfica unipolar
 * es aquella que admite una partición en un clan y un cluster, por ende, una
 * gráfica co-unipolar admite una partición en un conjunto independiente y una
 * gráfica multipartita completa; una gráfica co-bisplit no es otra cosa que una
 * gráfica counipolar con la restricción adicional de que la parte multipartita
 * completa es bipartita completa)
 */
public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
  
      /* Impresión de instrucciones en pantalla */
      Scanner sc = new Scanner(System.in);
      sc.useDelimiter("\n");

      System.out.print("\n\nSeleccione la acción que desea realizar:\n" +
      "\n\t(0) Decide si una gráfica contiene una obstrucción minima para las " +
      "\n\t    gráficas bisplit o alguna subgráfica isomorfa a P5 o House\n" +
      // [AGREGAR SUCESIÓN DE GRADOS, DAR LA PARTICIÓN COMPLETA,
      // NOMBRAR LA OBSMIN NO SOLO SU G6,
      // DEVOLVER LAS SUCESIONES DE GRADOS DE P5 Y HOUSE EN ORDEN DE TRAZADO]
      "\n\t(1) A partir de una lista de gráficas en formato g6, decidir qué " +
      "\n\t    gráficas en dicha lista son obstrucciones mínimas para las gráficas " +
      "\n\t    co-bisplit\n" +
      //
      "\n\t(6) Calcular el complemento de todas las gráficas en una lista\n" +
      // [JUNTAR 1 Y 6 PARA QUE SE REVISEN GRÁFICAS BISPLIT EN VEZ DE
      // CO-BISPLIT]
      "\n\nEscriba el número de su elección seguido de Enter: ");

      // System.out.println("\n");
  
      /* Procesado de la elección del usuario */
      switch (sc.nextLine()) {
        case "0":
                BisplitrestringidoP5cP5(args);
                break;
        case "1":
                // reconocimientoMasivocoBisplit(args);
                break;
        case "6":
                // calculoMasivoComplementos(args);
                break;
        default:
                System.out.println("\nArgumento inválido." +
                "El programa terminará.\n");
                System.exit(0);
      }
      sc.close();
    }
  


/* # # # # # # # # # # # # #  MÉTODOS  PRINCIPALES  # # # # # # # # # # # # # */



/**
 * Decide si una gráfica G en formato g6 es bisplit, y si contiene alguna de las
 * gráficas P5 o house como subgráfica inducida.
 *
 * Si G es bisplit devuelve como sí-certificado una partición bisplit.
 *
 * En otro caso, devuelve como no-certificado una subgráfica inducida que es
 * obstrucción mínima para bisplit.
 *
 * Si G contiene como subgráfica inducida a P5 o a house, devuelve el conjunto
 * de vértices que induce a dicha subgráfica.
 *
 */
private static void BisplitrestringidoP5cP5(String[] args) {
    if (args.length!=0){
        usoIndividual();
    }
    Scanner sc = new Scanner(System.in);
    System.out.printf("\n\nIngrese una gráfica G en formato g6: ");
    Grafica G = new Grafica(sc.nextLine());

    System.out.printf(G.esObsminBisplit_oP5_oHouse());

    System.out.printf(G.encuentraP5());

    System.out.printf(G.encuentraHouse());

    sc.close();
}




/* # # # # # # # # # # # # #  MÉTODOS  AUXILIARES  # # # # # # # # # # # # # */



/**
 * Imprime la sintaxis correcta para el uso del programa
 */
private static void usoIndividual() {
    System.out.println("\nuso: java -jar target/unipolarIdentifier-1.0.jar\n");
    System.exit(0);
}

}
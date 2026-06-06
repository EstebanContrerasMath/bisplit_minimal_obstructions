package BisplitMinimalObstructions;

import java.util.*;

/**
 * Clase para representar gráficas simples.
 *
 * Una gráfica tiene
 * - nombre (su representación como cadena de texto en formato g6)
 * - matriz (su matriz de adyacencia)
 * - lista (lista de adyacencias)
 * - orden (cantidad de vértices)
 * - aristas (lista de aristas).
 */
public class Grafica {

// ATRIBUTOS

/* Nombre en g6 */
private String nombre;
/* Matriz de adyacencia */
private int[][] matriz;
/* Lista de adyacencias */
// private LinkedList<LinkedList<Integer>> lista;
private ArrayList<LinkedList<Integer>> lista;
/* Orden */
private int orden;
/* Lista de aristas */
private LinkedList<Integer[]> aristas;


// **************************************************************  CONSTRUCTORES


/**
 * Define el estado inicial de una gráfica a partir de su representación en
 * g6.
 * 
 * @param g6 Una representación de la gráfica en formato g6.
 */
public Grafica (String g6) {
  this.nombre = g6;
  this.matriz = Traduccion.traduce(g6);
  this.lista = lista(this.matriz);
  this.orden = matriz.length;
  this.aristas = aristas(this.matriz);
}

/**
 * Define el estado inicial de una gráfica a partir de su matriz de
 * adyacencia.
 * @param matriz la matriz de adyacencia.
 */
public Grafica (int[][] matriz) {
  this.nombre = Traduccion.traduceAg6(matriz);
  this.matriz = matriz;
  this.orden = matriz.length;
  this.aristas = aristas(matriz);
  this.lista = lista(this.matriz);
}


// **********************************************************  GETTERS Y SETTERS


/**
 * Regresa el nombre de la gráfica.
 * @return el nombre de la gráfica.
 */
public String getNombre() {
  return this.nombre;
}

/**
 * Regresa la matriz de la gráfica.
 * @return la matriz de la gráfica.
 */
public int[][] getMatriz() {
  return this.matriz;
}

/**
 * Regresa la lista de adyacencia de la gráfica.
 * @return la lista de adyacencia de la gráfica.
 */
public ArrayList<LinkedList<Integer>> getLista() {
  return this.lista;
}

/**
 * Regresa el orden de la gráfica.
 * @return el orden de la gráfica.
 */
public int getOrden() {
  return this.orden;
}

/**
 * Regresa las aristas de la gráfica.
 * @return la lista de aristas de la gráfica.
 */
public LinkedList<Integer[]> getAristas() {
  return this.aristas;
}


// *************************************************************  COMPORTAMIENTO


/* # # # # # # # # # # # # #  MÉTODOS  PRINCIPALES  # # # # # # # # # # # # # */
  

/**
 * Imprime en pantalla el resultado de verificar si la gráfica que manda a
 * llamar el método es o no una gráfica bisplit???
 * 
 * @return
 */
public String esObsminBisplit_oP5_oHouse() {
  Grafica F = this.complemento();
  if (F.esCoBisplitSiCertificado()) {
    return "";
  } else {
    LinkedList<Integer> verticesSobran = new LinkedList<>();

    boolean flagObsmin = true;

    for (int i=0; i<F.getOrden(); i++) {
      Grafica H = F.agregarAristasIncidentes(new LinkedList<>(Arrays.asList(i)));
      if (!H.esCoBisplit()) {
        flagObsmin = false;
        verticesSobran.add(i);
        F = new Grafica(H.getMatriz());
      }
    }

    if (flagObsmin) {
      return "\n\n\tLa gráfica recibida es una OBSTRUCCIÓN MÍNIMA para Bisplit.\n\n";
    } else {
      // System.out.println("\nG = " + this.matrixToString());

      F = this.borrarVertices(verticesSobran);

      String s = F.getNombre() + " = G - {";
      for (Integer i : verticesSobran) {
        s = s + i + ", ";
      }
      s = s + "} ";
      
      return "\n\n\tLa gráfica recibida no admite partición Bisplit porque " + s +
      "es una obstrucción mínima para Bisplit.\n\n";
    }
  }
}

/**
 * Devuelve false si la gráfica que manda a llamar el método NO admite una
 * partición coBisplit.
 * 
 * En caso de que la gráfica sí admita una partición coBisplit, devuelve true, y en el
 * proceso imprime en pantalla el sí certificado (la partición)
 * 
 * @return
 */
public boolean esCoBisplitSiCertificado() {
  LinkedList<Integer> esCoBisplitCertificador = this.esCoBisplitCertificador();
  if (esCoBisplitCertificador!=null) {
    System.out.printf("\n\n\tLa gráfica admite una partición Bisplit, a saber, " +
    "donde el independiente es %s\n\n",esCoBisplitCertificador.toString());
    return true;
  }
  return false;
}

/**
 * Si la gráfica recibida es coBisplit, devuelve una partición coBisplit
 * representada por los vértices en el clan.
 *
 * Si la gráfica recibida NO es coBisplit, devuelve una lista vacía.
 *
 * @return
 */
public LinkedList<Integer> esCoBisplitCertificador() {
  LinkedList<LinkedList<Integer>> clanesMaximales = this.clanesMaximales();
  
  for (LinkedList<Integer> A : clanesMaximales) {
    Grafica H = this.borrarVertices(A);

    int HesClusterModif = H.esClusterModif();

    if (HesClusterModif>=0 && HesClusterModif<3) {
      return A;
    }
  }
  LinkedList<Integer> L = null;
  return L;
}

/**
 * Determina si una gráfica es coBisplit
 * 
 * @return true si el complemento de la gráfica es bisplit y false en otro caso
 */
public boolean esCoBisplit() {
  LinkedList<LinkedList<Integer>> clanesMaximales = this.clanesMaximales();
  
  for (LinkedList<Integer> A : clanesMaximales) {
    Grafica H = this.borrarVertices(A);
    if (H.esClusterModif()>=0 && H.esClusterModif()<3) {
      return true;
    }
  }
  return false;
}

/**
 * Algoritmo sí-certificador para determinar si una gráfica recibida tiene como
 * subgráfica inducia a la gráfica HOUSE.
 *
 * En caso afirmativo identifica un subconjunto de vértices que induce a la
 * gráfica House.
 *
 * @return Si la gráfica contiene como subgráfica inducida a la gráfica House,
 * devuelve como String un conjunto de vértices que inducen a la gráfica House.
 * Si G no contiene a House como subgráfica inducida se devuelve un mensaje con
 * dicha información.
*/
public String encuentraHouse() {

if (this.orden>4) {
    // 1. Calcular todas las subgráficas de orden 5

  LinkedList<LinkedList<Integer>> subconjuntos5 = this.generaNsubconjuntos(5);

    // 2. Dentro de las subgráficas calculadas, buscar una con sucesión de
    //    grados (3, 3, 2, 2, 2). Cada que aparezca alguna determinar si es
    //    bipartita. En caso de encontrar un candidato que NO SEA BIPARTITA,
    //    dicho subconjunto induce la gráfica house

  LinkedList<Integer> degSeqObjetivo = new LinkedList<>(Arrays.asList(3,3,2,2,2));
    for (LinkedList<Integer> VH : subconjuntos5) {
      Grafica H = this.inducida(VH);
      LinkedList<Integer> degSeqH = H.sucesionDeGrados();
      if (degSeqH.equals(degSeqObjetivo)) {
        if(!H.esBipartita()) {
          return "\n\tEl subconjunto de vértices " + VH + " induce la gráfica House\n\n";
        }
      }
    }
}
return "\n\tLa gráfica es libre de House\n\n";
}

/**
 * Algoritmo sí-certificador para determinar si una gráfica recibida tiene como
 * subgráfica inducia a P5.
 *
 * En caso afirmativo identifica un subconjunto de vértices que induce P5.
 *
 * @return Si la gráfica contiene como subgráfica inducida a P5, devuelve como
 * String un conjunto de vértices que inducen a P5. Si G no contiene a P5
 * como subgráfica inducida se devuelve un mensaje con dicha información.
*/
public String encuentraP5() {

  if (this.orden>4) {
    // 1. Calcular todas las subgráficas de orden 5

  LinkedList<LinkedList<Integer>> subconjuntos5 = this.generaNsubconjuntos(5);

    // 2. Dentro de las subgráficas calculadas, buscar una con sucesión de
    //    grados (2, 2, 2, 1, 1). Cada que aparezca alguna determinar si es
    //    conexa/bipartita. En caso de encontrar un candidato conexo dicho
    //    subconjunto induce P5

  LinkedList<Integer> degSeqObjetivo = new LinkedList<>(Arrays.asList(2,2,2,1,1));
    for (LinkedList<Integer> VH : subconjuntos5) {
      Grafica H = this.inducida(VH);
      LinkedList<Integer> degSeqH = H.sucesionDeGrados();
      if (degSeqH.equals(degSeqObjetivo)) {
        if(H.esConexa()) {
          return "\n\tEl subconjunto de vértices " + VH + " induce P5\n\n";
        }
      }
    }
    
  }
  return "\n\tLa gráfica es libre de P5\n\n";
}

/**
 * Devuelve la sucesión de grados no creciente de la gráfica que manda a
 * llamar el método
 * @return
 */
public LinkedList<Integer> sucesionDeGrados() {
  LinkedList<Integer> dS = new LinkedList<>();
  for (LinkedList<Integer> N : this.lista) {
    dS.add(N.size());
  }
  Collections.sort(dS);
  Collections.reverse(dS);
  return dS;
}

/**
 * Determina si la gráfica que manda a llamar el método es obstrucción mínima
 * para las gráficas co-Bisplit
 *
 * @return true si la gráfica es obstrucción minima para coBisplit, y false en
 * otro caso
 */
public boolean esObsminCoBisplit() {
  /* Verificar que sea obstrucción */
  if (this.esCoBisplit()) {
    return false;
  } else { /* Verificar minimalidad */
    for (int i=0; i<this.orden; i++) {
      Grafica H = this.borrarVertices(new LinkedList<>(Arrays.asList(i)));
      if (!H.esCoBisplit()) {
        return false;
      }
    }
  }
  return true;
}


/* # # # # # # # # # # # # #  MÉTODOS   AUXILIARES  # # # # # # # # # # # # # */
  
/**
 * Convierte una matrix de adyacencia, en un arreglo de listas de vecinos
 * (listas de adyacencias)
 *
 * @param matriz la matriz de adyacencia de una gráfica
 * @return La lista de adyacencias de la misma gráfica
 */
private ArrayList<LinkedList<Integer>> lista(int[][] matriz) {
  ArrayList<LinkedList<Integer>> lista = new ArrayList<LinkedList<Integer>>();
  int n = matriz.length;
  for (int i=0; i<n; i++) {
      LinkedList<Integer> Li = new LinkedList<>();
      for (int j=0; j<n; j++) {
          if (matriz[i][j]!=0) {
              Li.add(j);
          }
      }
      lista.add(Li);
  }
  return lista;
}

/**
 * Devuelve la lista de todas las aristas de la gráfica asociada a la matriz de
 * adyacencia recibida.
 *
 * @param matriz la matriz de adyacencia de una gráfica.
 * @return la lista de aristas de la gráfica asociada a la matriz recibida.
 */
private LinkedList<Integer[]> aristas(int[][] matriz) {
  LinkedList<Integer[]> lista = new LinkedList<>();
  for (int i=0; i<matriz.length; i++)
    for (int j=i+1; j<matriz.length; j++)
      if (matriz[i][j]==1) {
        Integer[] arista = {i,j};
        lista.add(arista);
      }
  return lista;
}

/**
 * Calcula el complemento de la gráfica que manda a llamar el método
 * 
 * @return el complemento de la gráfica
 */
public Grafica complemento() {
  int[][] Ac = new int[this.orden][this.orden];
  for (int i=0; i<this.orden; i++){
    for (int j=0; j<this.orden; j++){
      if (i==j) {
        Ac[i][j] = this.matriz[i][j];
      } else {
        Ac[i][j] = 1-this.matriz[i][j];
      }
    }
  }
  return new Grafica(Ac);
}

/**
 * Encuentra todos los clanes maximales de una gráfica
 * 
 * @return Una lista de listas de enteros.
 * Cada clan maximal de la gráfica está representado como un elemento de la
 * lista que se devuelve.
 */
public LinkedList<LinkedList<Integer>> clanesMaximales() {
  LinkedList<LinkedList<Integer>> todosLosClanes = todosLosClanes();
  LinkedList<LinkedList<Integer>> clanesMaximales = new LinkedList<>();
  for (LinkedList<Integer> clan : todosLosClanes) {
    boolean clanEsMaximal = true;
    for (int i=0; i<this.orden; i++) {
      if (!clan.contains(i)) {
        LinkedList<Integer> clanMayor = new LinkedList<>();
        boolean iYaFueAgregado = false;
        for (Integer j : clan) {
          if (j<i) {
            clanMayor.add(j);
          } else if (!iYaFueAgregado) {
            clanMayor.add(i);
            iYaFueAgregado = true;
            clanMayor.add(j);
          } else {
            clanMayor.add(j);
          }
        }
        if (!iYaFueAgregado) {
          clanMayor.add(i);
        }

        if (todosLosClanes.contains(clanMayor)) {
          clanEsMaximal = false;
          break;
        }
      }
    }
    if (clanEsMaximal) {
      clanesMaximales.add(clan);
    }
  }
  return clanesMaximales;
}

/**
 * Encuentra todos los clanes de una gráfica
 * 
 * @return Una lista de listas de enteros.
 * Cada clan de la gráfica está representado como un elemento de la lista que se
 * devuelve.
 */
public LinkedList<LinkedList<Integer>> todosLosClanes() {
  Queue<LinkedList<Integer>> Q = new LinkedList<>();
  LinkedList<LinkedList<Integer>> clanes = new LinkedList<>();
  int n = this.orden;

  /* Agregar conjunto vacío */
  LinkedList<Integer> vacio = new LinkedList<Integer>(Arrays.asList());
    Q.add(vacio);
    clanes.add(vacio);

  /* Agregar singuletes */
  for (int i=0; i<n; i++) {
    LinkedList<Integer> singulete = new LinkedList<Integer>(Arrays.asList(i));
    Q.add(singulete);
    clanes.add(singulete);
  }

  /* Agregar aristas */
  for (int i=0; i<n; i++)
    for (int j=i+1; j<n; j++)
      if (this.matriz[i][j]==1) {
        LinkedList<Integer> arista = new LinkedList<Integer>(Arrays.asList(i,j));
        Q.add(arista);
        clanes.add(arista);
      }

  /* Agregar clanes con 3 */
  for (int i=0; i<n; i++)
    for (int j=i+1; j<n; j++)
      for (int k=j+1; k<n; k++)
        if (this.matriz[i][j]==1 && this.matriz[i][k]==1 && this.matriz[j][k]==1) {
          LinkedList<Integer> TresClan = new LinkedList<Integer>(Arrays.asList(i,j,k));
          Q.add(TresClan);
          clanes.add(TresClan);
        }
  
  /* Agregar clanes con 4 o más vértices */
  while (!Q.isEmpty()) {
    LinkedList<Integer> x = Q.remove();
    if (x.size()>2) {
      int m = max(x);

      for (int i=m+1; i<n; i++) {
        /* Defino y como xU{i} */
        LinkedList<Integer> y = new LinkedList<>(x);
        y.add(i);

        if (y.size()>3 && y.getLast()<n) {
          /* Si y es clan, lo agrego a clanes y a Q */
          boolean yEsClan = true;
          for (Integer e : y) {
            LinkedList<Integer> z = new LinkedList<>(y);
            z.remove(e);
            if (!clanes.contains(z)) {
              yEsClan = false;
              break;
            }
          }
          if (yEsClan) {
            clanes.add(y);
            Q.add(y);
          }          
        }
      }
    }
  }
  return clanes;
}

/**
 * Calcula el máximo de una lista de enteros
 * 
 * @param x una lista de enteros
 * @return El valor máximo entre las entradas de x
 */
private int max(LinkedList<Integer> L) {
  int m = 0;
  for (Integer i : L)
    m = Integer.max(m, i);
  return m;
}

/**
 * Devuelve la gráfica obtenida a partir de la gráfica que manda a llamar el
 * método al borrar los vértices en la lista que se pasa como parámetro
 * 
 * @param X los vértices a borrar
 * @return La gráfica resultante al borrar los vértices en X
 */
public Grafica borrarVertices(LinkedList<Integer> X) {
  LinkedList<Integer> Xc = new LinkedList<>();
  for (int i=0; i<this.orden; i++) {
    if (!X.contains(i)) {
      Xc.add(i);
    }
  }
  return this.inducida(Xc);
}

/**
 * Devuelve la subgráfica inducida por el conjunto de vértices X
 * 
 * @param X es un subconjunto de vértices
 * @return La subgráfica inducida por X
 */
public Grafica inducida(LinkedList<Integer> X) {
  int[][] M = this.matriz;
  int[][] newM = new int[X.size()][X.size()];

  /* i,j indican la posición que leo en M, e I,J indican la posición que escribo
  en newM */
  int I = 0;
  int J = 0;
  for(int i=0; i<this.orden; i++) {
    J = 0;
    if (X.contains(i)) {
      for(int j=0; j<this.orden; j++) {
        if (X.contains(j)) {
          newM[I][J] = M[i][j];
          J++;
        }
      }
      I++;
    }
  }
  return new Grafica(newM);
}

/**
 * Determina si la gráfica recibida es o no un cluster.
 *
 * @return Si la gráfica recibida es un cluster, devuelve el número de
 * componentes conexas de la gráfica. En otro caso, el método devuelve -1.
 */
public Integer esClusterModif() {
  LinkedList<LinkedList<Integer>> componentes = this.calculaComponentes();
  
  /* Revisar que cada componente conexa es completa */
  for (LinkedList<Integer> Vi : componentes) {
    Grafica Gi = this.inducida(Vi);
    if (!Gi.esCompleta()) {
      return -1;
    }
  }

  return (componentes.size());
}

/**
 * Calcula las componentes conexas de una gráfica.
 *
 * @return Devuelve una lista de listas de enteros, en la que cada elemento es
 * la lista de vértices en una de las componentes de la gráfica que manda a
 * llamar el método
 */
public LinkedList<LinkedList<Integer>> calculaComponentes() {
  LinkedList<Integer> coloreados = new LinkedList<>();
  LinkedList<LinkedList<Integer>> componentes = new LinkedList<>();
  
  /* Identificar los conjuntos de vértices de las componentes conexas */
  while (coloreados.size()!=this.orden) {
    LinkedList<Integer> Vi = new LinkedList<>();
    /* Defino la raiz r como el 1er vértice que no haya sido coloreado */
    int r = 0;
    for (int i=0; i<this.orden; i++) {
      if (!coloreados.contains(i)) {
        r = i;
        break;
      }
    }
    /* Identificar vía BFS a los vértices de la gráfica que están en la misma
    componente que el vértice r */
    Queue<Integer> Q = new LinkedList<>();
    coloreados.add(r);
    Q.add(r);
    Vi.add(r);
    while (!Q.isEmpty()) {
      Integer x = Q.remove();
      for (Integer vecino : this.lista.get(x)) {
        if (!coloreados.contains(vecino)) {
          coloreados.add(vecino);
          Q.add(vecino);
          Vi.add(vecino);
        }
      } 
    }
    componentes.add(Vi);
  }
  return (componentes);
}

/**
 * Determina si la gráfica que manda a llamar el método es una gráfica completa
 * 
 * @return true si la gráfica es completa y false en caso contrario
 */
public boolean esCompleta() {
  int n = this.orden;
  for (LinkedList<Integer> vecindad : this.lista) {
    if (vecindad.size()!=n-1) {
      return false;
    }
  }
  return true;
}

/**
 * Devuelve la gráfica que se obtiene al hacer universales todos los vértices de
 * la lista X
 * 
 * @param X nuevos vértices universales
 * @return Una modificación de la gráfica donde todos los vértices de X son
 * universales 
 */
public Grafica agregarAristasIncidentes(LinkedList<Integer> X){
  int[][] newM = new int[this.orden][this.orden];
  for (int i=0; i<this.orden; i++) {
    for (int j=0; j<this.orden; j++) {
      newM[i][j] = this.matriz[i][j];
    }
  }

  for (Integer i : X) {
    for (int j=0; j<this.orden; j++) {
      if(i!=j) {
        newM[i][j] = 1;
        newM[j][i] = 1;
      }
    }
  }
  return (new Grafica(newM));
}

/**
 * Devuelve una representación en String de la matriz de adyacencia de la
 * gráfica 
 * @return
 */
public String matrixToString() {
  int[][] m = this.matriz;
  int n = m.length;
  String s = "";
  for (int i=0; i<n; i++) {
      s = s + "\n\t";
      for (int j=0; j<n; j++) {
          s = s + m[i][j] + " ";
      }
  }
  return s;
}

/**
 * Genera todos los n-subconjuntos de vértices la gráfica que manda a llamar al
 * método
 *
 * @param n un entero positivo menor o igual al orden de la gráfica que manda a
 * llamar el método
 * @return Una lista  con todos los n-subconjuntos de vértices
 */
public LinkedList<LinkedList<Integer>> generaNsubconjuntos(Integer n) {
  /* Sn almacenará los n-subconjuntos */
  LinkedList<LinkedList<Integer>> Sn = new LinkedList<>();
  /* binarios es una lista donde cada elemento representa un número binario de
  exactamente this.orden posiciones con exactamente n entradas 1 */
  List<Integer[]> binarios = generaBinarios(this.orden,n);
  /* Quiero leer cada elemento de binarios e interpretarlo como un subconjunto
  de vértices para conseguir la subgráfica inducida por ese subconjunto de
  vértices */
  for (Integer[] bin : binarios) {
    /* Convertir el binario a un subconjunto de vértices, y añadir dicha
    subgráfica a Sn*/
    LinkedList<Integer> X = new LinkedList<Integer>();
    for (int i=0; i<bin.length; i++) {
      if (bin[i]==1) {
        X.add(i);
      }
    }
    Sn.add(X);
  }
  return Sn;
}

/**
 * Devuelve una lista donde cada elemento representa en forma de arreglo a un
 * número binario con N posiciones donde exactamente n de ellas son 1
 *
 * @param N es la cantidad de posiciones en los números binarios que se van a
 * generar
 * @param n es la cantidad de entradas 1 que habrá en cada número binario
 * generado
 * @return
 */
public static List<Integer[]> generaBinarios(Integer N, Integer n) {
  List<Integer[]> binarios = new ArrayList<Integer[]>();
  Integer[] numero = new Integer[N];
  for (int i=0; i<n; i++) {
    numero[i] = 1;
  }
  for (int i=n; i<N; i++) {
    numero[i] = 0;
  }
  binarios.add(Arrays.copyOf(numero, numero.length));
  for (int l=1; l<binomial(N,n); l++) {
    int posicionUltimoUno = -1;
    int contadorDeUnos = 0;
    for(int i=0; i<N-1; i++) {
      if (numero[i]==1 && numero[i+1]==0) {
          posicionUltimoUno = i;
      }
    }
    for(int i=0; i < posicionUltimoUno; i++) {
      if (numero[i]==1) {
        contadorDeUnos++;
      }
    }
    numero[posicionUltimoUno]=0;

    /* Rellenar con los unos faltantes y luego puro cero */
    int hastaAquiLosUnos = posicionUltimoUno +n - contadorDeUnos +1;
    for (int j=posicionUltimoUno+1; j<hastaAquiLosUnos; j++) {
      numero[j] = 1;
    }
    for (int j=hastaAquiLosUnos; j<N; j++) {
      numero[j] = 0;
    }
    binarios.add(Arrays.copyOf(numero, numero.length));
  }
  return binarios;
}

/**
 * Calcula el coeficiente binomial binom(n)(k)
 * 
 * Se asume que 0 <= k <=n
 * 
 * @param n un entero
 * @param k un entero
 * @return binom(n)(k)
 */
private static Integer binomial(int n, int k)
  {
      if (k>n-k)
          k=n-k;

      Integer b=1;
      for (int i=1, m=n; i<=k; i++, m--)
          b=b*m/i;
      return b;
  }

/**
 * Decide si la gráfica que manda a llamar el método es bipartita
 * 
 * @return true si la gráfica que manda a llamar el método es bipartita y
 * false en otro caso
 */
public boolean esBipartita() {
  return this.biparticion()!=null;
}

/**
 * Devuelve una bipartición de la gráfica que llama al método siempre que
 * dicha gráfica sea bipartita. En otro caso devuelve null;
 * 
 * @return
 */
public LinkedList<LinkedList<Integer>> biparticion() {

  LinkedList<Integer> nivelesPares = new LinkedList<>();
  LinkedList<Integer> nivelesImpares = new LinkedList<>();
  boolean[] visitado = new boolean[this.orden];

  for (int r = 0; r < this.orden; r++) {

      if (visitado[r]) continue;

      int[][] resultadoBFS = this.BFS(r);
      int[] nivel = resultadoBFS[1];
      
      for (int v = 0; v < this.orden; v++) {


          if (nivel[v] < 0) continue;

          visitado[v] = true;

          if (nivel[v] % 2 == 0) {
              nivelesPares.add(v);
          } else {
              nivelesImpares.add(v);
          }

          // verificación local de bipartición
          for (int u = 0; u < this.orden; u++) {
              if (this.matriz[v][u] == 1 && nivel[u] >= 0) {
                  if ((nivel[v] % 2) == (nivel[u] % 2)) {
                      return null;
                  }
              }
          }
      }
  }

  LinkedList<LinkedList<Integer>> biparticion = new LinkedList<>();
  biparticion.add(nivelesPares);
  biparticion.add(nivelesImpares);
  return biparticion;
}

/**
 * Algoritmo BFS desde el vértice r de la gráfica que manda a llamar el método.
 *
 * Devuelve una matriz de dos renglones, donde el primero es la función de
 * parentesco y el segundo es la función de nivel.
 * 
 * @param r raíz desde la que se realizará BFS
 * @return funciones de parentesco y nivel resumidas en una matriz
 */
public int[][] BFS(int r) {
  String g6 = this.nombre;
  int[][] matriz = Traduccion.traduce(g6);
  int n = matriz.length;
  if (r<0 || r>n-1) {
    throw new IllegalArgumentException("r fuera de rango");
  }
  int[] padre = new int[n];
  int[] nivel = new int[n];
  for (int i=0; i<n; i++) {
    padre[i]=-2;
    nivel[i]=-2;
  }
  Queue<Integer> Q = new ArrayDeque<>();
  boolean[] visitado = new boolean[n];
  visitado[r] = true;
  Q.add(r);
  padre[r]=-1;
  nivel[r]=0;
  while (!Q.isEmpty()) {
    int x = Q.poll();
    for (int y=0; y<n; y++) {
      if (matriz[x][y]==1 && !visitado[y]) {
        visitado[y]=true;
        Q.add(y);
        padre[y]=x;
        nivel[y]=nivel[x]+1;
      }
    }
  }
  return new int[][]{padre, nivel};
}


/**
 * Decide si la gráfica es conexa
 * 
 * @return true si la gráfica es conexa y false en otro caso
 */
public boolean esConexa() {
  LinkedList<Integer> coloreados = new LinkedList<>();
  LinkedList<LinkedList<Integer>> componentes = new LinkedList<>();
  
  /* Identificar los conjuntos de vértices de las componentes conexas */
  while (coloreados.size()!=this.orden) {
    LinkedList<Integer> Vi = new LinkedList<>();
    /* Defino la raiz r como el 1er vértice que no haya sido coloreado */
    int r = 0;
    for (int i=0; i<this.orden; i++) {
      if (!coloreados.contains(i)) {
        r = i;
        break;
      }
    }
    /* Identificar vía BFS a los vértices de la gráfica que están en la misma
    componente que el vértice r */
    Queue<Integer> Q = new LinkedList<>();
    coloreados.add(r);
    Q.add(r);
    Vi.add(r);
    while (!Q.isEmpty()) {
      Integer x = Q.remove();
      for (Integer vecino : this.lista.get(x)) {
        if (!coloreados.contains(vecino)) {
          coloreados.add(vecino);
          Q.add(vecino);
          Vi.add(vecino);
        }
      } 
    }
    componentes.add(Vi);
  }

  return (componentes.size()==1);
}


//   /**
//    * 
//    */
//   public boolean sonAdyacentes(int u, int v) { //   <---------------------------------- ya no va a existir 
//     return this.matriz[u][v]==1;
//   }



























//   /**
//    * Imprime la matrix de adyacencia de la gráfica.
//    */
//   public void printMatriz() {
//     String s = "Matriz de adyacencias:" + this.matrixToString();
//     System.out.printf(s);
//   }

//   /**
//    * Imprime la lista de adyacencia de la gráfica.
//    */
//   public String listaToString() {
//     ArrayList<LinkedList<Integer>> lista = this.lista;
//     int n = lista.size();
//     String s = "";
//     for (int i=0; i<n; i++) {
//         s = s + "\n\t" + i + "\t(";
//         int d = lista.get(i).size();
//         for (int j=0; j<d; j++) {
//             s = s + lista.get(i).get(j) + ", ";
//         }
//         s = s + ")";
//     }
//     s = s + "\n\n";
//     return s;
//   }

//   /**
//    * Decide si la gráfica que manda a llamar el método es o no escindible
//    * @return
//    */
//   public boolean esEscindible() {
//     LinkedList<Integer> dS = this.sucesionDeGrados();
//     int[] DS = new int[dS.size()];
//     int i = 0;
//     for (Integer di : dS) {
//       DS[i] = di;
//       i++;
//     }

//     // Cálculo de p = max {i | di \ge i-1}
//     int p = 0;
//     for (int k=0; k<DS.length; k++) {
//       if (DS[k]>=k) {
//         p = k;
//       } else {
//         break;
//       }
//     }

//     /* Revisar la condición \sum_{i=1}^{p} d_i = p(p-1) + \sum_{i=p+1}^{n} d_i */
//     int S = 0;
//     for (int j=0; j<p; j++) {
//       S = S + DS[j];
//     }
//     int T = 0;
//     for (int j=p; j<DS.length; j++) {
//       T = T + DS[j];
//     }

//     return (S == (p*(p-1) + T));
//   }

//   /**
//    * Decide si la gráfica que manda a llamar el método es o no escindible
//    * @return
//    */
//   public boolean esEscindibleComentado() {
//     LinkedList<Integer> dS = this.sucesionDeGrados();
    
//     System.out.println("\ndS = " + dS.toString() + "\n");
    
//     int[] DS = new int[dS.size()];
//     int i = 0;
//     for (Integer di : dS) {
//       DS[i] = di;
//       i++;
//     }

//     String sDS = "DS = [";
//     for(int u=0; u<DS.length; u++){
//       sDS = sDS + DS[u] + " ";
//     }
//     sDS = sDS + "]";
//     System.out.println(sDS);

//     // Cálculo de p = max {i | di \ge i-1}
//     int p = 0;
//     for (int k=0; k<DS.length; k++) {
//       if (DS[k]>=k) {
//         p = k;
//       } else {
//         break;
//       }
//     }
//     System.out.println("p = " + p + "\n");

//     /* Revisar la condición \sum_{i=1}^{p} d_i = p(p-1) + \sum_{i=p+1}^{n} d_i */
//     int S = 0;
//     for (int j=0; j<p; j++) {
//       S = S + DS[j];
//     }
//     int T = 0;
//     for (int j=p; j<DS.length; j++) {
//       T = T + DS[j];
//     }

//     System.out.println("S = " + S);
//     System.out.println("T = " + T);
//     System.out.println("p*(p-1) + T = " + (p*(p-1) + T));
    
//     System.out.println("return = " + (S == (p*(p-1) + T)));

//     return (S == (p*(p-1) + T));
//   }


//     /**
//    * 
//    * @return
//    */
//   public boolean esUnipolar() {
//     if (this.esEscindible()) {
//       return true;
//     } else {
//       LinkedList<LinkedList<Integer>> clanesMaximales = this.clanesMaximales();
//       String noCertificado = "NO es unipolar. " + 
//                                 "A continuación se presenta un no certificado:\n";
      
//       for (LinkedList<Integer> A : clanesMaximales) {
//         Grafica H = this.borraAristasIncidentes(A);
//         LinkedList<Integer> esCluster = H.esCluster();
  
//         if (esCluster.equals(new LinkedList<>())) {
  
//           int[] b = new int[this.orden];
//           for (Integer i : A) {
//             b[i] = 1;
//           }
//           LinkedList<Integer> B = new LinkedList<>();
//           for (int i=0; i<this.orden; i++) {
//             if (b[i]==0) {
//               B.add(i);
//             }
//           }
//           return true;
//         } else {
//           noCertificado = noCertificado + "\t" + A.toString() +
//                           "\n\t\t" + H.encuentraP3() + "\n";
//         }
//       }
//       return false;
//     }
//   }

//   /**
//    * 
//    * @return
//    */
//   public boolean esUnipolarSiCertificado() {
//     if (this.esEscindible()) {
//       System.out.println("Es escindible XD");
//       return true;
//     } else {
//       LinkedList<LinkedList<Integer>> clanesMaximales = this.clanesMaximales();
//       String noCertificado = "NO es unipolar. " + 
//                                 "A continuación se presenta un no certificado:\n";
      
//       for (LinkedList<Integer> A : clanesMaximales) {
//         Grafica H = this.borraAristasIncidentes(A);
//         LinkedList<Integer> esCluster = H.esCluster();
  
//         if (esCluster.equals(new LinkedList<>())) {
  
//           int[] b = new int[this.orden];
//           for (Integer i : A) {
//             b[i] = 1;
//           }
//           LinkedList<Integer> B = new LinkedList<>();
//           for (int i=0; i<this.orden; i++) {
//             if (b[i]==0) {
//               B.add(i);
//             }
//           }
//           System.out.println("Es counipolar bajo la partición " + "I = " + A.toString() + " M = " + B.toString());
//           return true;
//         } else {
//           noCertificado = noCertificado + "\t" + A.toString() +
//                           "\n\t\t" + H.encuentraP3() + "\n";
//         }
//       }
//       return false;
//     }
//   }

//   /**
//    * 
//    * @return
//    */
//   public boolean esUnipolarComentado() {
//     LinkedList<LinkedList<Integer>> clanesMaximales = this.clanesMaximales();
//     String noCertificado = "NO es unipolar. " + 
//                               "A continuación se presenta un no certificado:\n";
    
//     for (LinkedList<Integer> A : clanesMaximales) {
//       Grafica H = this.borraAristasIncidentes(A);
//       LinkedList<Integer> esCluster = H.esCluster();

//       if (esCluster.equals(new LinkedList<>())) {

//         int[] b = new int[this.orden];
//         for (Integer i : A) {
//           b[i] = 1;
//         }
//         LinkedList<Integer> B = new LinkedList<>();
//         for (int i=0; i<this.orden; i++) {
//           if (b[i]==0) {
//             B.add(i);
//           }
//         }
//         System.out.println("tiene partición unipolar (A,B), donde \n\tA = "
//                         + A.toString() + " y \n\tB = " + B.toString());
//         return true;
//       } else {
//         noCertificado = noCertificado + "\t" + A.toString() +
//                         "\n\t\t" + H.encuentraP3() + "\n";
//       }
//     }

//     System.out.println(noCertificado);
//     return false;
//   }

//   /**
//    * Decide si G-v es unipolar
//    * @return
//    */
//   public boolean esUnipolar(int v) {
//     LinkedList<LinkedList<Integer>> clanesMaximales = this.clanesMaximales();
//     String noCertificado = "G-" + v + " = " +
//           this.borrarVertices(new LinkedList<>(Arrays.asList(v))).getNombre() +
//           " NO es unipolar. A continuación se presenta no certificado de ello:\n";
    
//     LinkedList<Integer> singV = new LinkedList<>(Arrays.asList(v));
//     for (LinkedList<Integer> A : clanesMaximales) {
//       if (!A.equals(singV)) {
//         Grafica H = this.borraAristasIncidentes(A);
//         LinkedList<Integer> esCluster = H.esCluster();
  
//         if (esCluster.equals(new LinkedList<>())) {
  
//           int[] b = new int[this.orden];
//           b[v] = 1;
//           for (Integer i : A) {
//             b[i] = 1;
//           }
//           LinkedList<Integer> B = new LinkedList<>();
//           for (int i=0; i<this.orden; i++) {
//             if (b[i]==0) {
//               B.add(i);
//             }
//           }
//           // System.out.println("G-" + v + " tiene partición unipolar " +
//           //                   "(A,B), donde \n\tA = " + A.toString() +
//           //                   " y \n\tB = " + B.toString() + "\n");
//           return true;
//         } else {
//           noCertificado = noCertificado + "\t" + A.toString() +
//                           "\n\t\t" + H.encuentraP3() + "\n";
//         }
//       }
//     }

//     // System.out.println(noCertificado);
//     return false;
//   }

//   public boolean esObsminUnipolar() {
//     if (this.esUnipolar()) {
//       return false;
//     } else {
//       for (int i=0; i<this.orden; i++) {
//         Grafica H = this.borraAristasIncidentes(new LinkedList<>(Arrays.asList(i)));
//         if (!H.esUnipolar(i)) {
//           return false;
//         }
//       }
//     }
//     return true;
//   }

//   /**
//    * 
//    * @return
//    */
//   public String esObsminUnipolarComentado() {
//     if (this.esUnipolar()) {
//       return "es unipolar.\n";
//     } else {
//       for (int i=0; i<this.orden; i++) {
//         Grafica H = this.borraAristasIncidentes(new LinkedList<>(Arrays.asList(i)));
//         if (!H.esUnipolar(i)) {
//           /* Imprimir G-i */
//           H = this.borrarVertices(new LinkedList<>(Arrays.asList(i)));
//           System.err.println("G-" + i + " = " + H.getNombre());
          
//           return "no es unipolar, pero G-" + i + " tampoco lo es.\n";
//         }
//       }
//     }
//     return "es una OBSTRUCCIÓN MÍNIMA para las gráficas unipolares.\n";
//   }

//   /**
//    * 
//    * @return
//    */
//   public String esObsminUnipolarComentadoCounipolares() {
//     if (this.esUnipolar()) {
//       return "es CoUnipolar.\n";
//     } else {
//       for (int i=0; i<this.orden; i++) {
//         Grafica H = this.borraAristasIncidentes(new LinkedList<>(Arrays.asList(i)));
//         if (!H.esUnipolar(i)) {
//           /* Calcular G-i */
//           H = this.borrarVertices(new LinkedList<>(Arrays.asList(i)));
          
//           return "no es CoUnipolar debido a que el complemento de G-" + i + ", " +
//                       H.complemento().getNombre() + " , no es unipolar.\n";
//         }
//       }
//     }
//     return "es una OBSTRUCCIÓN MÍNIMA para las gráficas CoUnipolares.\n";
//   }

//   /**
//    * 
//    * @return
//    */
//   public Grafica encuentraObsminUnipolar() {
//     Grafica G = new Grafica(this.matriz);
//     if (G.esUnipolar()) {
//       return null;
//     }
//     boolean flag = true;
//     while (flag) {
//       flag = false;
//       for (int i=0; i<G.getOrden(); i++) {
//         Grafica H = G.borrarVertices(new LinkedList<>(Arrays.asList(i)));
//         if (!H.esUnipolar()) {
//           G = H;
//           flag = true;
//           break;
//         }
//       }
//     }
//     return G;
//   }




// /*  ** ** ** ** ** ** ** MÉTODOS AUXILIARES ** ** ** ** ** ** ** ** ** ** **  */



//   public int[][] floydWarshall() {
//       int[][] result = this.matriz;
//       int INF = 100000000;
//       for (int i=0; i<result.length; i++) {
//         for (int j=0; j<result.length; j++) {
//           if (i!=j && result[i][j]==0) {
//             result[i][j]=INF;
//           }
//         }
        
//       }
  
//       int V = result.length;
  
//       // Add all vertices one by one to
//       // the set of intermediate vertices.
//       for (int k = 0; k < V; k++) {
  
//           // Pick all vertices as source one by one
//           for (int i = 0; i < V; i++) {
  
//               // Pick all vertices as destination
//               // for the above picked source
//               for (int j = 0; j < V; j++) {
  
//                   // shortest path from
//                   // i to j 
//                   if(result[i][k] != 1e8 && result[k][j]!= 1e8)
//                   result[i][j] = Math.min(result[i][j],result[i][k] + result[k][j]);
//               }
//           }
//       }
//       return result;
//     }





//   /**
//    * Determina el número de clan de una gráfica
//    * @return El número de clan de la gráfica que manda a llamar el método
//    */
//   public Integer numClan() {
//     Integer numClan = 1;
//     Queue<LinkedList<Integer>> Q = new LinkedList<>();
//     LinkedList<LinkedList<Integer>> clanes = new LinkedList<>();
//     int n = this.orden;

//     /* Agregar singuletes */
//     for (int i=0; i<n; i++) {
//       LinkedList<Integer> singulete = new LinkedList<Integer>(Arrays.asList(i));
//       Q.add(singulete);
//       clanes.add(singulete);
//     }

//     /* Agregar aristas */
//     for (int i=0; i<n; i++)
//       for (int j=i+1; j<n; j++)
//         if (this.matriz[i][j]==1) {
//           LinkedList<Integer> arista = new LinkedList<Integer>(Arrays.asList(i,j));
//           Q.add(arista);
//           clanes.add(arista);
//           numClan = 2;
//         }

//     /* Agregar clanes con 3 */
//     for (int i=0; i<n; i++)
//       for (int j=i+1; j<n; j++)
//         for (int k=j+1; k<n; k++)
//           if (this.matriz[i][j]==1 && this.matriz[i][k]==1 && this.matriz[j][k]==1) {
//             LinkedList<Integer> TresClan = new LinkedList<Integer>(Arrays.asList(i,j,k));
//             Q.add(TresClan);
//             clanes.add(TresClan);
//             numClan = 3;
//           }
    
//     /* Agregar clanes con 4 o más vértices */
//     while (!Q.isEmpty()) {
//       LinkedList<Integer> x = Q.remove();
//       if (x.size()>2) {
//         int m = max(x);

//         for (int i=m+1; i<n; i++) {
//           /* Defino y como xU{i} */
//           LinkedList<Integer> y = new LinkedList<>(x);
//           y.add(i);
  
//           if (y.size()>3 && y.getLast()<n) {
//             /* Si y es clan, lo agrego a clanes y a Q */
//             boolean yEsClan = true;
//             for (Integer e : y) {
//               LinkedList<Integer> z = new LinkedList<>(y);
//               z.remove(e);
//               if (!clanes.contains(z)) {
//                 yEsClan = false;
//                 break;
//               }
//             }
//             if (yEsClan) {
//               clanes.add(y);
//               Q.add(y);
//               numClan = Math.max(numClan,y.size());
//             }          
//           }
//         }
//       }
//     }
//     return numClan;
//   }


//   /**
//    * Decide si la gráfica recibida es o no un cluster.
//    * @return Si la gráfica no es un cluster, devuelve el conjunto de vertices de
//    * una componente que no es completa. De otro modo, devuelve una lista vacia.
//    */
//   public LinkedList<Integer> esCluster() {
//     LinkedList<Integer> coloreados = new LinkedList<>();
//     LinkedList<LinkedList<Integer>> componentes = new LinkedList<>();
    
//     /* Identificar los conjuntos de vértices de las componentes conexas */
//     while (coloreados.size()!=this.orden) {
//       LinkedList<Integer> Vi = new LinkedList<>();
//       /* Defino la raiz r como el 1er vértice que no haya sido coloreado */
//       int r = 0;
//       for (int i=0; i<this.orden; i++) {
//         if (!coloreados.contains(i)) {
//           r = i;
//           break;
//         }
//       }
//       /* Identificar vía BFS a los vértices de la gráfica que están en la misma
//       componente que el vértice r */
//       Queue<Integer> Q = new LinkedList<>();
//       coloreados.add(r);
//       Q.add(r);
//       Vi.add(r);
//       while (!Q.isEmpty()) {
//         Integer x = Q.remove();
//         for (Integer vecino : this.lista.get(x)) {
//           if (!coloreados.contains(vecino)) {
//             coloreados.add(vecino);
//             Q.add(vecino);
//             Vi.add(vecino);
//           }
//         } 
//       }
//       componentes.add(Vi);
//     }

//     /* Revisar que cada componente conexa es completa */
//     for (LinkedList<Integer> Vi : componentes) {
//       Grafica Gi = this.inducida(Vi);
//       if (!Gi.esCompleta()) {
//         return Vi;
//       }
//     }
//     return (new LinkedList<>());
//   }







//   /**
//    * Para cada n-subconjunto V' de vértices de this, genera la subgráfica
//    * inducida de this por V' y la agrega a una lista de gráficas que serán la
//    * salida del método
//    * @param n un entero positivo menor o igual al orden de this
//    * @return La lista de todas las subgráficas inducidas de this de orden n
//    */
//   public LinkedList<Grafica> generaSubgraficasInducidas(Integer n) {
//     /* Sn almacenará las subgráficas inducidas de orden n de G */
//     LinkedList<Grafica> Sn = new LinkedList<>();
//     /* binarios es una lista donde cada elemento representa un número binario de
//     exactamente this.orden posiciones con exactamente n entradas 1 */
//     List<Integer[]> binarios = generaBinarios(this.orden,n);
//     /* Quiero leer cada elemento de binarios e interpretarlo como un subconjunto
//     de vértices para conseguir la subgráfica inducida por ese subconjunto de
//     vértices */
//     for (Integer[] bin : binarios) {
//       /* Conseguir la subgráfica inducida por el subconjunto de vértices que
//       representa bin, y añadir dicha subgráfica a Sn*/
//       LinkedList<Integer> X = new LinkedList<Integer>();
//       for (int i=0; i<bin.length; i++) {
//         if (bin[i]==1) {
//           X.add(i);
//         }
//       }

//       Grafica H = this.inducida(X);
//       Sn.add(H);
//     }

//     return Sn;
//   }

//   // private static String arregloImprimible(Integer[] A) {
//   //   String s = "";
//   //   for (Integer i : A) {
//   //     s = s + i + " ";
//   //   }
//   //   return s;
//   // }

//   public Grafica borraAristasIncidentes(LinkedList<Integer> X){
//     int[][] newM = new int[this.orden][this.orden];
//     for (int i=0; i<this.orden; i++) {
//       for (int j=0; j<this.orden; j++) {
//         newM[i][j] = this.matriz[i][j];
//       }
//     }

//     for (Integer i : X) {
//       for (int j=0; j<this.orden; j++) {
//         newM[i][j] = 0;
//         newM[j][i] = 0;
//       }
//     }
//     return (new Grafica(newM));
//   }




//   /**
//    * 
//    * @return
//    */
//   public Grafica complemento() {
//     int[][] Ac = new int[this.orden][this.orden];
//     for (int i=0; i<this.orden; i++){
//       for (int j=0; j<this.orden; j++){
//         if (i==j) {
//           Ac[i][j] = this.matriz[i][j];
//         } else {
//           Ac[i][j] = 1-this.matriz[i][j];
//         }
//       }
//     }
//     return new Grafica(Ac);
//   }

//   /**
//    * Asuminedo que la gráfica que manda a llamar este método NO ES UN CLUSTER,
//    * encuentra un conjunto de 3 vértices que inducen P3
//    * @return
//    */
//   public String encuentraP3(){
//     /* Determinar un vértice r que sea extremo de un P3*/
//     LinkedList<Integer> noCompleta = this.esCluster();
//     int r = -1;
//     for (Integer i : noCompleta) {
//       if (this.lista.get(i).size()<(noCompleta.size()-1)) {
//         r = i;
//         break;
//       }
//     }

//     /* Hacer BFS con raiz r */
//     Queue<Integer> Q = new LinkedList<>();
//     Integer[] p = new Integer[this.orden];
//     Integer[] l = new Integer[this.orden];
//     p[r]=r;
//     l[r]=0;
//     Q.add(r);
//     while(!Q.isEmpty()) {
//       int x = Q.remove();
//       for (Integer y : this.lista.get(x)) {
//         if (Objects.isNull(p[y])) {
//           p[y] = x;
//           l[y] = l[x]+1;
//           Q.add(y);
          
//           if (l[y]==2) {
//             Q.clear();
//           }
//         }
//       }
//     }

//     /* Encontrar un vértice y a distancia 2 de r. entonces {y, p[y], r} induce P3*/
//     int y = -1;
//     for (int i=0; i<this.orden; i++) {
//       if (!Objects.isNull(p[i]) && l[i]==2) {
//         y = i;
//         break;
//       }
//     }

//     LinkedList<Integer> cert = new LinkedList<>(Arrays.asList(y,p[y],r));
//     return ("el conjunto " + cert.toString() + " induce P3");
//   }




































  }

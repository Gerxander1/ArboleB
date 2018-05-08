package btree;

import java.util.Arrays;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

public class Btree {

    public static Nodo raiz = null;
    public static String[] dibujo = null;

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        int[] n = new int[8];

        System.out.println("Bienbenido....");
        System.out.println("Por Favor ingrese un maximo de 8 numeros");

        for (int i = 0; i < 8; i++) {
            n[i] = teclado.nextInt();
        }
            System.out.println("......el arbol B es...... ");
        int max =3;

        Btree.dibujo = new String[max];
        for (int i = 0; i < 8; i++) {
            insert(buscar(raiz, n[i]), n[i]);
        }

        System.out.print("");

        for (int i = 0; i < max; i++) { 

            Btree.dibujo[i] = "";

        }
        valoresPorNivel(raiz, 0);
        for (int i = 0; i < max; i++) { 
            System.out.println(Btree.dibujo[i]);
              
        }
      
    }

    public static class Nodo {

        public Integer nodeType;
        public Integer v1;
        public Integer v2;
        public Nodo menor;
        public Nodo medio;
        public Nodo mayor;
        public Nodo padre;

        public Nodo(Integer nodeType, Integer s, Integer l, Nodo padre) {
            this.nodeType = nodeType;
            this.v1 = s;
            this.v2 = l;
            this.menor = null;
            this.medio = null;
            this.mayor = null;
            this.padre = padre;
        }
    }

    public static String valoresPorNivel(Nodo nodoA, Integer nivel) {
        String result = "";
        if (nodoA != null) {
            result += valoresPorNivel(nodoA.menor, nivel + 1);
            if (nodoA.v1 != null) {
                Btree.dibujo[nivel] += "[" + nodoA.v1 + "]";
            }
            result += valoresPorNivel(nodoA.medio, nivel + 1);
            if (nodoA.v2 != null) {
                Btree.dibujo[nivel] += "[" + nodoA.v2 + "]";
            }
            result += valoresPorNivel(nodoA.mayor, nivel + 1);
        }
        return result;
    }

    public static Nodo buscar(Nodo nodoA, Integer valor) {
        if (nodoA != null) {
            if (nodoA.nodeType == 3) { // rama
                if (nodoA.v1 >= valor && nodoA.v2 <= valor) {
                    return nodoA;
                } else if (valor < nodoA.v1) {
                    return nodoA.menor == null ? nodoA : buscar(nodoA.menor, valor);
                } else if (valor > nodoA.v2) {
                    return nodoA.mayor == null ? nodoA : buscar(nodoA.mayor, valor);
                } else {
                    return nodoA.medio == null ? nodoA : buscar(nodoA.medio, valor);
                }
            } else if (nodoA.v1 == valor) {
                return nodoA;
            } else if (valor < nodoA.v1) {
                return nodoA.menor == null ? nodoA : buscar(nodoA.menor, valor);
            } else {
                return nodoA.mayor == null ? nodoA : buscar(nodoA.mayor, valor);
            }
        }
        return null;
    }

    public static void insert(Nodo nodoA, Integer valor) {
        if (nodoA == null) {
            raiz = new Nodo(2, valor, null, null);
            return;
        } else if (nodoA.nodeType == 2) {
            nodoA.nodeType = 3;
            nodoA.v2 = Math.max(valor, nodoA.v1);
            nodoA.v1 = Math.min(valor, nodoA.v1);
        } else {
            reorganizar(nodoA, valor, null);
        }
    }

    public static void reorganizar(Nodo nodoA, Integer valor, Nodo remainder) {
        Nodo padre;
        Nodo small, large;
        SortedMap<Integer, Nodo> nodeList = new TreeMap<Integer, Nodo>();

        if (nodoA.padre == null) {
            padre = new Nodo(null, null, null, null);
        } else {
            padre = nodoA.padre;
        }

        small = new Nodo(2, Math.min(nodoA.v1, Math.min(nodoA.v2, valor)), null, padre);
        large = new Nodo(2, Math.max(nodoA.v1, Math.max(nodoA.v2, valor)), null, padre);

        if (nodoA.menor != null && nodoA.medio != null && nodoA.mayor != null) {
            nodeList.put(nodoA.menor.v1, nodoA.menor);
            nodeList.put(nodoA.mayor.v1, nodoA.mayor);
            nodeList.put(nodoA.medio.v1, nodoA.medio);
            nodeList.put(remainder.v1, remainder);

            small.menor = popNodeList(nodeList);
            small.mayor = popNodeList(nodeList);
            large.menor = popNodeList(nodeList);
            large.mayor = popNodeList(nodeList);

            small.menor.padre = small;
            small.mayor.padre = small;
            large.menor.padre = large;
            large.mayor.padre = large;

        }

        int median = mediana(nodoA.v1, nodoA.v2, valor);
        if (padre.nodeType == null) {
            padre.nodeType = 2;
            padre.v1 = median;
            padre.menor = small;
            padre.mayor = large;
            raiz = padre;
            return;
        }
        if (padre.nodeType == 2) {
            padre.nodeType = 3;
            padre.v1 = Math.min(median, padre.v1);
            padre.v2 = Math.max(median, padre.v1);
            if (padre.menor == nodoA) {
                if (small.v1 < padre.v1) {
                    padre.medio = large;
                    padre.menor = small;
                } else {
                    padre.medio = small;
                    padre.menor = large;
                }
            } else if (small.v1 < padre.v1) {
                padre.medio = large;
                padre.mayor = small;
            } else {
                padre.medio = small;
                padre.mayor = large;
            }
            return;
        }
        if (padre.nodeType == 3) {
            if (padre.menor == nodoA) {
                padre.menor = large;
                reorganizar(padre, median, small);
            } else {
                padre.mayor = small;
                reorganizar(padre, median, large);
            }
        }
    }

    public static Integer mediana(int i, int j, int k) {
        int[] valueList = {i, j, k};
        Arrays.sort(valueList);
        return valueList[1];
    }

    public static Nodo popNodeList(SortedMap<Integer, Nodo> nodeList) {
        if (nodeList.isEmpty() == false) {
            Nodo temp = nodeList.get(nodeList.firstKey());
            nodeList.remove(nodeList.firstKey());
            return temp;
        } else {
            return null;
        }
    }
}

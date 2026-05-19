package org.example;

import java.util.List;

public class DSU {
   private final int vertices;
   int [] sz;
   int[] par ;
   DSU(int n){
       this.vertices=n;
       par=new int[n];
       sz=new int[n];
       for(int i=0;i<n;i++){
           sz[i]=1;
           par[i]=i;
       }
   }
   int leader(int x){
       if (par[x] == x) {
           return x;
       }
       return par[x] = leader(par[x]);
   }
   boolean join (int x,int y){
       x=leader(x);y=leader(y);
       if(x==y)return false;
       if (sz[x] > sz[y]) {
           int temp = x;
           x = y;
           y = temp;
       }
       par[x] = y;
       sz[y] += sz[x];
       return true;
   }

}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';

import { Compra } from 'src/app/models/compra';
import { global } from '../global';

import Swal from 'sweetalert2';
import { TipoComprobante } from '../../models/tipo-comprobante';
import { DetalleCompra } from 'src/app/models/detalle-compra';


@Injectable({
  providedIn: 'root'
})
export class CompraService {

  url: string;
  compraKey = 'compraJson';

  constructor
  (
    private httpClient: HttpClient
  ) 
  {
    this.url = global.url;
  }

  /**
   * Recibe los datos de la compra que se esta creando para guardarlos en el localStorage del navegador en caso de que
   * la vista de creación se pierda y se necesito recuperar la información para posteriormente guardarla.
   * @param compra Los valores de tipo Compra que se esta creando
   * 
   */
  almacenarCompra(compra: Compra): void {
    localStorage.setItem(this.compraKey, JSON.stringify(compra));
  }

  /**
   * Obtiene  los datos de una compra previamente guardada en el local storage
   * @returns compraData Devuelve un objeto de tipo Compra con los datos almacenados
   */
  obtenerCompra(): Compra | null {
    const compraData = localStorage.getItem(this.compraKey);
    return compraData ? JSON.parse(compraData) as Compra : null;
  }

  agregarItemCompra(itemCompra: DetalleCompra): void {
    const compra = this.obtenerCompra();
    if(compra) {
      compra.items.push(itemCompra);
      this.almacenarCompra(compra);
    }
  }

  limpiarCompra(): void {
    localStorage.removeItem(this.compraKey);
  }

  getCompras(): Observable<Compra[]> 
  {
    return this.httpClient.get<Compra[]>(`${this.url}/compras`);
  }

  getCompra(id: number): Observable<any> 
  {
    return this.httpClient.get<any>(`${this.url}/compras/${id}`).pipe(
      catchError(e => 
        {
          Swal.fire(e.error.mensaje, e.error.error, 'error');
          return throwError(e);
        })
    );
  }

  create(compra: Compra): Observable<any> 
  {
    return this.httpClient.post<any>(`${this.url}/compras`, compra).pipe(
      catchError(e => 
        {
          Swal.fire(e.error.mensaje, e.error.error, 'error');
          return throwError(e);
        })
    );
  }

  disable(compra: Compra): Observable<any>
  {
    return this.httpClient.put<any>(`${this.url}/compras/disable`, compra).pipe(
      catchError(e => 
        {
          Swal.fire(e.error.mensaje, e.error.error, 'error');
          return throwError(e);
        })
    );
  }

  delete(idcompra: number): Observable<any> {
    return this.httpClient.delete<any>(`${this.url}/compras/eliminar/${idcompra}`).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error')
        return throwError(e);
      })
    );
  } 

  getTiposComprobante(): Observable<TipoComprobante[]> 
  {
    return this.httpClient.get<TipoComprobante[]>(`${this.url}/compras/tipos-comprobante/get`);
  }
}

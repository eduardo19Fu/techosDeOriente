import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';

import { Compra } from 'src/app/models/compra';
import { global } from '../global';

import Swal from 'sweetalert2';
import { TipoComprobante } from '../../models/tipo-comprobante';


@Injectable({
  providedIn: 'root'
})
export class CompraService {

  url: string;

  constructor
  (
    private httpClient: HttpClient
  ) 
  {
    this.url = global.url;
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

  getTiposComprobante(): Observable<TipoComprobante[]> 
  {
    return this.httpClient.get<TipoComprobante[]>(`${this.url}/compras/tipos-comprobante/get`);
  }
}

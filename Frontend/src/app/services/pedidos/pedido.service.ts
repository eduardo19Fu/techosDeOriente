import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { global } from '../global';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import Swal from 'sweetalert2';
import { Pedido } from 'src/app/models/pedido';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {

  url: string;

  constructor(
    private http: HttpClient
  ) {
    this.url = global.url;
  }

  getPedidos(): Observable<any> {
    return this.http.get<any>(`${this.url}/pedidos`).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error, 'error');
        return throwError(e);
      })
    );
  }

  getPedido(id: number): Observable<any> {
    return this.http.get<any>(`${this.url}/pedidos/${id}`).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    )
  }

  create(pedido: Pedido): Observable<any> {
    return this.http.post<any>(`${this.url}/pedidos`, pedido).pipe(
      catchError((e) => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  delete(id: number): Observable<any> {
    return this.http.delete<any>(`${this.url}/pedidos/delete/${id}`).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  /************ REPORTES PDF ***************/
  getPedidoPDF(idpedido: number): Observable<any>{
    const headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');
    const requestOptions: any = { headers, responseType: 'blob' };

    return this.http.get<any>(`${this.url}/pedidos/generate-pedido/${idpedido}`, requestOptions).pipe(
      map((response: any) => {
        return{
          filename: 'pedido.pdf',
          data: new Blob([response], { type: 'application/pdf' })
        };
      })
    );
  }
}

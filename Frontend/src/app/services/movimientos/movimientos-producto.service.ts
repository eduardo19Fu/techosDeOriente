import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { map, catchError, filter } from 'rxjs/operators';

import { MovimientoProducto } from 'src/app/models/movimiento-producto';

import { global } from '../global';
import Swal from 'sweetalert2';
import { TipoMovimiento } from 'src/app/models/tipo-movimiento';

@Injectable({
  providedIn: 'root'
})
export class MovimientosProductoService {

  url: string;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.url = global.url;
  }

  getMovimientosProducto(): Observable<MovimientoProducto[]> {
    return this.http.get<MovimientoProducto[]>(`${this.url}/movimientos`);
  }

  getMovimientosProductoPage(idproducto: number, page: number): Observable<any> {
    return this.http.get<any>(`${this.url}/movimientos/${idproducto}/${page}`).pipe(
      map((response: any) => {
        (response.content as MovimientoProducto[]).map(movimientoProducto => {
          return movimientoProducto;
        });
        return response;
      })
    );
  }

  getTiposMovimiento(): Observable<TipoMovimiento[]> {
    return this.http.get<TipoMovimiento[]>(`${this.url}/movimientos/tipos-movimiento/get`);
  }

  create(movimientoProducto: MovimientoProducto): Observable<any> {
    return this.http.post<any>(`${this.url}/movimientos`, movimientoProducto).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  /********* INVENTARIO FORMATO PDF ***********/
  getInventoryPDF(fechaIni: Date, fechaFin: Date): Observable<any> {
    const headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');
    const requestOptions: any = { headers, responseType: 'blob' };

    return this.http.post(`${this.url}/movimientos/inventario?fechaIni=${fechaIni.toString()}&fechaFin=${fechaFin.toString()}`,
      '', requestOptions).pipe(

      map((response: any) => {
        return {
          filename: 'inventario.pdf',
          data: new Blob([response], { type: 'application/pdf' })
        };
      })
    );
  }
}

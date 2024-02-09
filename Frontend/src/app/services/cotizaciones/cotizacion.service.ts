import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { Cotizacion } from 'src/app/models/cotizacion';
import { global } from '../global';

import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class CotizacionService {

  url: string;

  constructor(
    private httpClient: HttpClient
  ) {
    this.url = global.url;
  }

  getCotizaciones(): Observable<Cotizacion[]> {
    return this.httpClient.get<Cotizacion[]>(`${this.url}/cotizaciones`).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  getCotizacion(id: number): Observable<any> {
    return this.httpClient.get(`${this.url}/cotizacion/${id}`).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  create(cotizacion: Cotizacion): Observable<any> {
    return this.httpClient.post<any>(`${this.url}/cotizaciones/create`, cotizacion).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  delete(id: number): Observable<any> {
    return this.httpClient.delete<any>(`${this.url}/cotizaciones/delete/${id}`).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  getCotizacionPDF(id: number): Observable<any> {
    const headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');
    const requestOptions: any = { headers, responseType: 'blob' };

    return this.httpClient.get<any>(`${this.url}/cotizaciones/generate/${id}`, requestOptions).pipe(
      map((response: any) => {
        return{
          filename: 'factura.pdf',
          data: new Blob([response], { type: 'application/pdf' })
        };
      })
    );
  }
}

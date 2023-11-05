import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { Envio } from 'src/app/models/envio';

import { global } from '../global';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class EnvioService {

  url: string;

  constructor(
    private httpClient: HttpClient
  ) {
    this.url = global.url;
  }

  getEnvios(): Observable<Envio[]> {
    return this.httpClient.get<Envio[]>(`${this.url}/envios`);
  }

  getEnvio(id: number): Observable<any> {
    return this.httpClient.get<any>(`${this.url}/envios/${id}`).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  create(envio: Envio): Observable<any> {
    return this.httpClient.post<any>(`${this.url}/envios`, envio).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  dispatch(envio: Envio): Observable<any> {
    return this.httpClient.put<any>(`${this.url}/envios/despachar`, envio).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  delete(id: number): Observable<any> {
    return this.httpClient.delete<any>(`${this.url}/envios/${id}`).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  /**
   * Función que permite la comunicación necesaria para generar el reporte cuando un envío
   * es creado.
   * @param idenvio
   */

  getEnvioPdf(idenvio: number): Observable<any> {
    const headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');
    const requestOptions: any = { headers, responseType: 'blob' };

    return this.httpClient.get<any>(`${this.url}/envios/generate/${idenvio}`, requestOptions).pipe(
      map((response: any) => {
        return {
          filename: 'envio.pdf',
          data: new Blob([response], {type: 'application/pdf'})
        };
      })
    );
  }

  /**
   * función que permite llevar a cabo la creación del reporte de envios realizados según la fecha dada
   * @param fecha Permite filtrar los envios realizados por fecha
   * 
   */
  getEnviosRealizadosPdf(fecha: Date): Observable<any> {
    const headers = new HttpHeaders();
    headers.append('Accept', 'application/pdf');
    const requestOptions: any = { headers, responseType: 'blob' };

    return this.httpClient.get<any>(`${this.url}/envios/realizados?fecha=${fecha.toString()}`, requestOptions).pipe(
      map((response: any) => {
        return {
          filename: 'reporte-envios.pdf',
          data: new Blob([response], {type: 'application/pdf'})
        };
      })
    );
  }
}

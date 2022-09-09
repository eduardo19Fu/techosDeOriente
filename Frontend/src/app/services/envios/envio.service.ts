import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

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
}

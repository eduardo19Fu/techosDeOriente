import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { Pais } from '../../models/pais';
import { global } from '../global';

import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class PaisService {

  url: string;

  constructor(
    private httpClient: HttpClient
  ) 
  {
    this.url = global.url;
  }

  getPaises(): Observable<Pais[]>{
    return this.httpClient.get<Pais[]>(`${this.url}/paises`).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  getPais(id: number): Observable<any>{
    return this.httpClient.get<any>(`${this.url}/paises/${id}`).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  create(pais: Pais): Observable<any> {
    return this.httpClient.post<any>(`${this.url}/paises`, pais).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  update(pais: Pais): Observable<any> {
    return this.httpClient.put<any>(`${this.url}/paises`, pais).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

}

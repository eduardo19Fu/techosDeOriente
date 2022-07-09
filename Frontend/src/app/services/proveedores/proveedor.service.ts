import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { Proveedor } from '../../models/proveedor';

import { global } from '../global';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class ProveedorService {

  url: string;

  constructor
    (
      private httpClient: HttpClient
    ) {
    this.url = global.url;
  }

  getProveedores(): Observable<Proveedor[]> {
    return this.httpClient.get<Proveedor[]>(`${this.url}/proveedores`).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  getProveedor(id: number): Observable<any> {
    return this.httpClient.get<any>(`${this.url}/proveedores/${id}`).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  create(proveedor: Proveedor): Observable<any> {
    return this.httpClient.post<any>(`${this.url}/proveedores`, proveedor).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    );
  }

  update(proveedor: Proveedor): Observable<any> {
    return this.httpClient.put<any>(`${this.url}/proveedores`, proveedor).pipe(
      catchError(e => {
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(e);
      })
    )
  }
}

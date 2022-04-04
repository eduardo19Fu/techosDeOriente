import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ClienteCreateService {

  modal = false;

  constructor() { }

  abrirModal(): void{
    this.modal = true;
  }

  cerrarModal(): void{
    this.modal = false;
  }
}

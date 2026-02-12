import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; 

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, FormsModule], //  FormsModule aqui
  templateUrl: './cadastro.html',
  styleUrls: ['./cadastro.css']
})
export class CadastroComponent {
  usuario = { nome: '', email: '', senha: '', role: '' };
  onSubmit() { console.log(this.usuario); }
}
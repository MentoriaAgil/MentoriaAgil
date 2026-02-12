import { Routes } from '@angular/router';
import { CadastroComponent } from './cadastro/cadastro'; 

export const routes: Routes = [
  { path: 'cadastro', component: CadastroComponent },
  { path: '', redirectTo: '/cadastro', pathMatch: 'full' }
];
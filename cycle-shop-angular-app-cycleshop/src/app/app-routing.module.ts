import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginFormComponent } from './login-form/login-form.component';
import { CyclesComponent } from './cycles/cycles.component';
import { RestockComponent } from './restock/restock.component';
import { CartComponent } from './cart/cart.component';
import { BaseLayoutComponent } from './base-layout/base-layout.component';
import { SiteLayoutComponent } from './site-layout/site-layout.component';
import { CheckoutComponent } from './checkout/checkout.component'; // Import the CheckoutComponent

const routes: Routes = [
  {
    path: '',
    component: BaseLayoutComponent,
    children: [
      { path: 'login', component: LoginFormComponent },
    ],
  },
  {
    path: '',
    component: SiteLayoutComponent,
    children: [
      { path: 'cycles', component: CyclesComponent },
      { path: 'restock', component: RestockComponent },
      { path: 'cart', component: CartComponent },
      { path: 'checkout', component: CheckoutComponent }, // Add this route
    ],
  },
  { path: '', redirectTo: '/cycles', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}

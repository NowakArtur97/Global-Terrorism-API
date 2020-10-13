import { LayoutModule } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterModule } from '@angular/router';

import { NavComponent } from './nav/nav.component';
import { NavigationComponent } from './navigation/navigation.component';

const materialComponents = [
  LayoutModule,
  MatToolbarModule,
  MatSidenavModule,
  MatListModule,
  MatDialogModule,
  MatFormFieldModule,
  MatInputModule,
  MatButtonModule,
  MatCardModule,
  MatProgressSpinnerModule,
];

@NgModule({
  declarations: [NavigationComponent, NavComponent],
  imports: [CommonModule, RouterModule, materialComponents],
  exports: [NavComponent, materialComponents],
})
export class MaterialModule {}

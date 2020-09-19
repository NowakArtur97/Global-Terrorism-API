import { LayoutModule } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterModule } from '@angular/router';

import { NavigationComponent } from './navigation/navigation.component';

const materialComponents = [
  LayoutModule,
  MatToolbarModule,
  MatButtonModule,
  MatSidenavModule,
  MatListModule,
  MatDialogModule,
];

@NgModule({
  declarations: [NavigationComponent],
  imports: [CommonModule, RouterModule, materialComponents],
  exports: [NavigationComponent, materialComponents],
})
export class MaterialModule {}

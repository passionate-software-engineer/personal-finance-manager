import { NgModule } from '@angular/core';
import { OrderPipe } from './pfm-order.pipe';

@NgModule({
  declarations: [OrderPipe],
  exports: [OrderPipe],
  providers: [OrderPipe]
})
export class OrderModule {}

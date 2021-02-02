import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { LoadingSpinnerComponent } from './loading-spinner/loading-spinner.component';
import { PlaceholderDirective } from "./placeholder/placeholder.directive";
import { AlertComponent } from './alert/alert.component';
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";

@NgModule({
    declarations: [LoadingSpinnerComponent, PlaceholderDirective, AlertComponent],
    imports: [CommonModule, NgbModule],
    exports: [AlertComponent, LoadingSpinnerComponent, PlaceholderDirective, CommonModule, NgbModule],
    entryComponents: [AlertComponent]
})
export class SharedModule {}
import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './components/app/app.component';
import {HttpClientModule} from "@angular/common/http";
import {ModalComponent} from "./components/modal/modal.component";
import {FileUploadComponent} from "./components/fileUpload/file-upload.component";
import {MemeBrowserComponent} from "./components/memeBrowser/meme-browser.component";

@NgModule({
    declarations: [
        AppComponent,
        ModalComponent,
        FileUploadComponent,
        MemeBrowserComponent
    ],
    imports: [
        BrowserModule,
        HttpClientModule,
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}

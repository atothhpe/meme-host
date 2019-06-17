import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './components/app/app.component';
import {HttpClientModule} from "@angular/common/http";
import {ModalComponent} from "./components/modal/modal.component";
import {FileUploadComponent} from "./components/fileUpload/file-upload.component";
import {MemeBrowserComponent} from "./components/memeBrowser/meme-browser.component";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";

import {library} from '@fortawesome/fontawesome-svg-core';
import {faBan, faUpload} from '@fortawesome/free-solid-svg-icons';

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
        FontAwesomeModule
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {

    constructor() {
        // Add an icon to the library for convenient access in other components
        library.add(faBan);
        library.add(faUpload);
    }

}

import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {AppComponent} from './components/app/app.component';
import {HttpClientModule} from '@angular/common/http';
import {ModalComponent} from './components/modal/modal.component';
import {FileUploadComponent} from './components/fileUpload/file-upload.component';
import {MemeBrowserComponent} from './components/memeBrowser/meme-browser.component';
import {MemeViewerComponent} from './components/memeViewer/meme-viewer.component';

import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {library} from '@fortawesome/fontawesome-svg-core';
import {faBan, faTrash, faUpload} from '@fortawesome/free-solid-svg-icons';
import {MemeMainComponent} from './components/memeMain/meme-main.component';

import {VgCoreModule} from 'videogular2/core';
import {VgControlsModule} from 'videogular2/controls';
import {VgOverlayPlayModule} from 'videogular2/overlay-play';
import {VgBufferingModule} from 'videogular2/buffering';

const appRoutes: Routes = [
    {path: '', component: MemeMainComponent},
    {path: 'viewMeme', component: MemeViewerComponent},
    {path: 'viewMeme/:memeId', component: MemeViewerComponent},
];

@NgModule({
    declarations: [
        AppComponent,
        ModalComponent,
        FileUploadComponent,
        MemeMainComponent,
        MemeBrowserComponent,
        MemeViewerComponent
    ],
    imports: [
        BrowserModule,
        RouterModule.forRoot(
            appRoutes,
            {enableTracing: false} // <-- debugging purposes only
        ),
        HttpClientModule,
        FontAwesomeModule,
        VgCoreModule,
        VgControlsModule,
        VgOverlayPlayModule,
        VgBufferingModule
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {

    constructor() {
        // Add an icon to the library for convenient access in other components
        library.add(faBan);
        library.add(faUpload);
        library.add(faTrash);
    }

}

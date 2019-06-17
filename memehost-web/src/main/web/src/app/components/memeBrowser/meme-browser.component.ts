import {Component, ElementRef} from '@angular/core';
import {HttpClient, HttpEventType, HttpHeaders} from '@angular/common/http';
import {ViewChild} from '@angular/core';
import {ModalService} from '../../services/ModalService';
import {MemeService} from '../../services/MemeService';
import {FileMetadata} from '../../model/file-metadata';
import {environment} from '../../../environments/environment';

@Component({
    selector: 'meme-browser',
    templateUrl: './meme-browser.component.html',
    styleUrls: ['./meme-browser.component.css']
})
export class MemeBrowserComponent {

    private fileMetadata: FileMetadata[] = [];

    private noImageUrl =  'https://semantic-ui.com/images/wireframe/white-image.png';
    private memeService: MemeService;

    constructor(memeService: MemeService) {
        this.memeService = memeService;
        this.loadMemes();
    }

    loadMemes() {
        this.memeService
            .getAllFileMetadata()
            .subscribe((data: FileMetadata[]) => this.fileMetadata = data );
    }

    getImageLink(fileMetadata: FileMetadata): string {
        if (fileMetadata.mimeType.startsWith('image')) {
            if (fileMetadata.storageThumbnailName) {
                return environment.serverUrl + 'memes/' + fileMetadata.storageThumbnailName + '/data';
            }  else {
                return environment.serverUrl + 'memes/' + fileMetadata.storageFileName + '/data';
            }
        } else {
            return this.noImageUrl;
        }
    }

}

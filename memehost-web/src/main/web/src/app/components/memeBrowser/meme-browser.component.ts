import {Component} from '@angular/core';
import {MemeService} from '../../services/meme.service';
import {Meme} from '../../model/meme';
import {environment} from '../../../environments/environment';

@Component({
    selector: 'meme-browser',
    templateUrl: './meme-browser.component.html',
    styleUrls: ['./meme-browser.component.css']
})
export class MemeBrowserComponent {

    private readonly noImageUrl =  'https://semantic-ui.com/images/wireframe/white-image.png';
    private memes: Meme[] = [];

    constructor(private memeService: MemeService) {
        this.loadMemes();
        memeService.memeUploadedSubject.asObservable().subscribe(() => {
            this.loadMemes();
        });
    }

    loadMemes() {
        this.memeService
            .getAllFileMetadata()
            .subscribe((data: Meme[]) => this.memes = data);
    }

    deleteMeme(meme: Meme) {
        this.memeService
            .deleteMeme(meme)
            .subscribe(() => {
                this.loadMemes();
            });
    }

    getThumbnailLink(meme: Meme): string {
        if (meme.thumbnailFileMetadata) {
            return environment.serverUrl + 'memes/' + meme.thumbnailFileMetadata.id + '/thumbnailData';
        } else {
            return this.noImageUrl;
        }
    }

}

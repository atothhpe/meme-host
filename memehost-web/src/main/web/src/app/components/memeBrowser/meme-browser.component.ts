import {Component} from '@angular/core';
import {MemeService} from '../../services/meme.service';
import {Meme} from '../../model/meme';
import {Router} from '@angular/router';

@Component({
    selector: 'meme-browser',
    templateUrl: './meme-browser.component.html',
    styleUrls: ['./meme-browser.component.less']
})
export class MemeBrowserComponent {

    memes: Meme[] = [];

    constructor(private memeService: MemeService, private router: Router) {
        this.loadMemes();
        memeService.memeUploadedSubject.asObservable().subscribe(() => {
            this.loadMemes();
        });
    }

    loadMemes() {
        this.memeService
            .getAllMemes()
            .subscribe((data: Meme[]) => this.memes = data);
    }

    deleteMeme(meme: Meme) {
        this.memeService
            .deleteMeme(meme)
            .subscribe(() => {
                this.loadMemes();
            });
    }

    memeClicked(meme: Meme) {
        this.router.navigate(['viewMeme', meme.id]);
    }

}

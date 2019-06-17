import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {MemeService} from '../../services/meme.service';
import {Meme} from '../../model/meme';
import {VgAPI} from 'videogular2/core';

@Component({
    selector: 'meme-viewer',
    templateUrl: './meme-viewer.component.html',
    styleUrls: ['./meme-viewer.component.less']
})
export class MemeViewerComponent implements OnInit {

    meme: Meme;

    constructor(private route: ActivatedRoute, private memeService: MemeService) {
    }

    ngOnInit() {
        const memeId: string = this.route.snapshot.params['memeId'];
        if (memeId) {
            this.loadMemeById(memeId);
        } else {
            this.loadNextMeme();
        }
    }

    loadMemeById(memeId: string) {
        this.memeService.getMemeById(memeId).subscribe(
            meme => {
                this.meme = meme;
            },
            () => {
                this.meme = null;
            });
    }

    loadNextMeme() {
    }

    onPlayerReady(event: VgAPI) {
        event.fsAPI.toggleFullscreen(event.fsAPI.videogularElement);
    }

}

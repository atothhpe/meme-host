import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable, Subject} from 'rxjs';
import {Meme} from '../model/meme';

@Injectable({providedIn: 'root'})
export class MemeService {

    private http: HttpClient;
    public memeUploadedSubject: Subject<Meme> = new Subject<Meme>();

    private readonly noImageUrl =  'https://semantic-ui.com/images/wireframe/white-image.png';

    constructor(http: HttpClient) {
        this.http = http;
    }

    public getAllMemes(): Observable<Meme[]> {
        return this.http.get<Meme[]>(environment.serverUrl + 'memes');
    }

    public getMemeById(memeId: string): Observable<Meme> {
        return this.http.get<Meme>(environment.serverUrl + 'memes/' + memeId);
    }

    public deleteMeme(meme: Meme): Observable<any> {
        return this.http.delete(environment.serverUrl + 'memes/' + meme.id);
    }

    getThumbnailLink(meme: Meme): string {
        if (meme.thumbnailFileMetadata) {
            return environment.serverUrl + 'memes/' + meme.id + '/thumbnailData';
        } else {
            return this.noImageUrl;
        }
    }

    getMemeLink(meme: Meme): string {
        return environment.serverUrl + 'memes/' + meme.id + '/data';
    }
}

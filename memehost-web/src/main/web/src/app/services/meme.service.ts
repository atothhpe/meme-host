import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable, Subject} from 'rxjs';
import {Meme} from '../model/meme';

@Injectable({providedIn: 'root'})
export class MemeService {

    private http: HttpClient;
    public memeUploadedSubject: Subject<Meme> = new Subject<Meme>();

    constructor(http: HttpClient) {
        this.http = http;
    }

    public getAllFileMetadata(): Observable<Meme[]> {
        return this.http.get<Meme[]>(environment.serverUrl + 'memes');
    }

    public deleteMeme(meme: Meme): Observable<any> {
        return this.http.delete(environment.serverUrl + 'memes/' + meme.id);
    }
}

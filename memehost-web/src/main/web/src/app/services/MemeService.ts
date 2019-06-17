import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {FileMetadata} from '../model/file-metadata';
import {Observable} from 'rxjs';

@Injectable({providedIn: 'root'})
export class MemeService {

    private http: HttpClient;

    constructor(http: HttpClient) {
        this.http = http;
    }

    public getAllFileMetadata(): Observable<FileMetadata[]> {
        return this.http.get<FileMetadata[]>(environment.serverUrl + 'memes');
    }

    public deleteMeme(fileMetadata: FileMetadata) {
        this.http.delete(environment.serverUrl + 'memes/' + fileMetadata.storageFileName).subscribe(result => {

        });
    }
}

import {FileMetadata} from './file-metadata';

export interface Meme {
    id: string;
    fileMetadata: FileMetadata;
    thumbnailFileMetadata?: FileMetadata;
}

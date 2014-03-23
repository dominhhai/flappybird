//
//  Pipe.m
//  FlappyBird
//
//  Created by Hai Do Minh on 3/21/14.
//  Copyright (c) 2014 Hai Do Minh. All rights reserved.
//

#import "Pipe.h"

@implementation Pipe

@synthesize scene, type, sprTop, sprBottom;

-(id)initWithType:(PipeType)pType posX:(float)pX topY:(float)pTop range:(float)pRange scene:(CCScene*)pScene {
    self = [super init];
    if(!self) return nil;
    
    return self;
}

@end

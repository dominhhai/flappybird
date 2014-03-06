//
//  GameConfig.h
//  FlappyBird
//
//  Created by Hai Do Minh on 3/6/14.
//  Copyright (c) 2014 Hai Do Minh. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "cocos2d-ui.h"
#import "CCTextureCache.h"

@interface GameConfig : NSObject

+ (NSMutableDictionary*) atlasInfo;

+ (void) setAtlasInfo:(NSMutableDictionary*)val;

+(CCTexture*) atlas;

@end
